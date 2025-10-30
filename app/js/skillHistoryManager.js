/**
 * Skill History Manager
 * Handles skill history, navigation, and localStorage persistence
 */

import { 
  subscribe, 
  getStateSnapshot, 
  getCurrentSkill, 
  setCurrentSkill,
  updateSkillPartial,
  getSkillRegistry,
  createNewSkill as createNewSkillState
} from './state.js';

const STORAGE_KEY = 'skilltree_creator_history';
const HISTORY_VERSION = '1.0';

class SkillHistoryManager {
  constructor() {
    this.dom = {
      historyList: document.querySelector('[data-skill-history-list]'),
      historyEmpty: document.querySelector('[data-skill-history-empty]'),
      searchInput: document.querySelector('[data-skill-search]'),
      clearButton: document.querySelector('[data-clear-history]'),
      importButton: document.querySelector('[data-import-skill]'),
      importFileInput: document.querySelector('[data-import-file-input]'),
    };
    
    this.skills = new Map();
    this.filteredSkills = new Map();
    this.currentFilter = '';
    this.unsubscribe = null;
    
    this.initializeEventListeners();
    this.loadFromStorage();
  }
  
  initializeEventListeners() {
    if (this.dom.searchInput) {
      this.dom.searchInput.addEventListener('input', (event) => {
        this.currentFilter = event.target.value.toLowerCase();
        this.filterSkills();
        this.renderHistory();
      });
    }
    
    if (this.dom.clearButton) {
      this.dom.clearButton.addEventListener('click', () => {
        if (confirm('Are you sure you want to clear all skills? This cannot be undone.')) {
          this.clearAllSkills();
        }
      });
    }
    
    if (this.dom.importButton && this.dom.importFileInput) {
      this.dom.importButton.addEventListener('click', () => {
        this.dom.importFileInput.click();
      });
      
      this.dom.importFileInput.addEventListener('change', (event) => {
        const file = event.target.files[0];
        if (file) {
          this.importSkillFromFile(file);
          // Reset file input
          event.target.value = '';
        }
      });
    }
    
    // Handle new skill button
    this.dom.newSkillButton = document.querySelector('[data-new-skill]');
    if (this.dom.newSkillButton) {
      this.dom.newSkillButton.addEventListener('click', () => {
        this.createNewSkill();
      });
    }
  }
  
  subscribeToState() {
    this.unsubscribe = subscribe((snapshot) => {
      this.syncWithState(snapshot);
      this.saveToStorage();
    });
  }
  
  syncWithState(snapshot) {
    // Update local skills map from state
    this.skills.clear();
    
    Object.entries(snapshot.skills || {}).forEach(([id, skill]) => {
      this.skills.set(id, {
        ...skill,
        lastModified: Date.now()
      });
    });
    
    this.filterSkills();
    this.renderHistory();
  }
  
  filterSkills() {
    this.filteredSkills.clear();
    
    if (!this.currentFilter) {
      this.filteredSkills = new Map(this.skills);
      return;
    }
    
    this.skills.forEach((skill, id) => {
      const searchableText = [
        id,
        skill.title || '',
        skill.tags || []
      ].join(' ').toLowerCase();
      
      if (searchableText.includes(this.currentFilter)) {
        this.filteredSkills.set(id, skill);
      }
    });
  }
  
  renderHistory() {
    if (!this.dom.historyList || !this.dom.historyEmpty) return;
    
    const currentSkillId = getCurrentSkill()?.id;
    const hasSkills = this.filteredSkills.size > 0;
    
    // Show/hide empty state
    this.dom.historyEmpty.hidden = hasSkills;
    
    // Clear current list
    this.dom.historyList.innerHTML = '';
    
    if (!hasSkills) return;
    
    // Sort skills by last modified (most recent first)
    const sortedSkills = Array.from(this.filteredSkills.entries())
      .sort(([, a], [, b]) => (b.lastModified || 0) - (a.lastModified || 0));
    
    sortedSkills.forEach(([id, skill]) => {
      const skillElement = this.createSkillElement(id, skill, currentSkillId === id);
      this.dom.historyList.appendChild(skillElement);
    });
  }
  
  createSkillElement(id, skill, isActive) {
    const element = document.createElement('div');
    element.className = 'skill-history-item';
    element.classList.toggle('skill-history-item--active', isActive);
    
    const title = skill.title || 'Untitled Skill';
    const skillId = id.split(':').pop() || id;
    const tags = Array.isArray(skill.tags) ? skill.tags.slice(0, 3) : [];
    
    element.innerHTML = `
      <div class="skill-history-item__main">
        <div class="skill-history-item__title" title="${title}">${title}</div>
        <div class="skill-history-item__id">${skillId}</div>
      </div>
      <div class="skill-history-item__meta">
        ${tags.length > 0 ? `
          <div class="skill-history-item__tags">
            ${tags.map(tag => `<span class="skill-history-item__tag">${tag}</span>`).join('')}
          </div>
        ` : ''}
        <div class="skill-history-item__actions">
          <button class="skill-history-item__load" data-skill-load="${id}" title="Load skill">
            📝
          </button>
          <button class="skill-history-item__delete" data-skill-delete="${id}" title="Delete skill">
            🗑️
          </button>
        </div>
      </div>
    `;
    
    // Add event listeners
    const loadButton = element.querySelector('[data-skill-load]');
    const deleteButton = element.querySelector('[data-skill-delete]');
    
    if (loadButton) {
      loadButton.addEventListener('click', (event) => {
        event.stopPropagation();
        this.loadSkill(id);
      });
    }
    
    if (deleteButton) {
      deleteButton.addEventListener('click', (event) => {
        event.stopPropagation();
        this.deleteSkill(id);
      });
    }
    
    // Make whole item clickable
    element.addEventListener('click', () => {
      this.loadSkill(id);
    });
    
    return element;
  }
  
  loadSkill(id) {
    const success = setCurrentSkill(id);
    if (!success) {
      console.error('Failed to load skill:', id);
      // Show error notification
      this.showNotification('Failed to load skill', 'error');
    }
  }
  
  deleteSkill(id) {
    if (confirm('Are you sure you want to delete this skill?')) {
      // Remove from state (this will trigger state update)
      const snapshot = getStateSnapshot();
      if (snapshot.skills[id]) {
        delete snapshot.skills[id];
        
        // Update registry
        const registry = snapshot.registry;
        const index = registry.indexOf(id.toLowerCase());
        if (index > -1) {
          registry.splice(index, 1);
        }
        
        // If we deleted the current skill, switch to another one or create new
        if (snapshot.currentSkillId === id) {
          const remainingSkills = Object.keys(snapshot.skills);
          if (remainingSkills.length > 0) {
            snapshot.currentSkillId = remainingSkills[0];
          } else {
            // Create new default skill
            this.createNewSkill();
          }
        }
        
        this.showNotification('Skill deleted successfully', 'success');
      }
    }
  }
  
  createNewSkill() {
    const snapshot = getStateSnapshot();
    const skillTreeName = snapshot.skills[snapshot.currentSkillId]?.skillTreeName || null;
    
    // Use the centralized createNewSkill function
    const result = createNewSkillState(skillTreeName);
    
    if (result.success) {
      this.showNotification('New skill created', 'success');
    } else {
      this.showNotification(`Failed to create skill: ${result.error}`, 'error');
    }
  }
  
  clearAllSkills() {
    const snapshot = getStateSnapshot();
    
    // Clear all skills
    snapshot.skills = {};
    snapshot.currentSkillId = null;
    snapshot.registry = [];
    
    // Create one default skill
    this.createNewSkill();
    
    this.showNotification('All skills cleared', 'success');
  }
  
  async importSkillFromFile(file) {
    try {
      const text = await file.text();
      const skillJSON = JSON.parse(text);
      
      // Validate structure - only ID is required
      if (!skillJSON.id) {
        throw new Error('Invalid skill JSON: missing required field "id"');
      }
      
      // Normalize ID to lowercase to prevent capitalization issues
      const originalId = skillJSON.id;
      skillJSON.id = skillJSON.id.toLowerCase().trim();
      
      if (originalId !== skillJSON.id) {
        console.warn(`Skill ID normalized from "${originalId}" to "${skillJSON.id}" to prevent capitalization issues`);
      }
      
      // Title is optional - use ID as fallback if missing
      if (!skillJSON.title) {
        skillJSON.title = skillJSON.id.split(':').pop() || 'Imported Skill';
      }
      
      const snapshot = getStateSnapshot();
      
      // Check if skill already exists
      if (snapshot.skills[skillJSON.id]) {
        if (!confirm('A skill with this ID already exists. Do you want to replace it?')) {
          return;
        }
      }
      
      // Add last modified timestamp
      skillJSON.lastModified = Date.now();
      
      // Add to state
      snapshot.skills[skillJSON.id] = skillJSON;
      snapshot.registry.push(skillJSON.id.toLowerCase());
      snapshot.currentSkillId = skillJSON.id;
      
      this.showNotification('Skill imported successfully!', 'success');
      
    } catch (error) {
      console.error('Import failed:', error);
      this.showNotification(`Failed to import skill: ${error.message}`, 'error');
    }
  }
  
  showNotification(message, type = 'info') {
    // Create notification element
    const notification = document.createElement('div');
    notification.className = `notification notification--${type}`;
    notification.textContent = message;
    
    // Add to page
    document.body.appendChild(notification);
    
    // Auto remove after 3 seconds
    setTimeout(() => {
      if (notification.parentNode) {
        notification.parentNode.removeChild(notification);
      }
    }, 3000);
  }
  
  saveToStorage() {
    try {
      const snapshot = getStateSnapshot();
      const historyData = {
        version: HISTORY_VERSION,
        skills: snapshot.skills,
        currentSkillId: snapshot.currentSkillId,
        registry: snapshot.registry,
        timestamp: Date.now()
      };
      
      localStorage.setItem(STORAGE_KEY, JSON.stringify(historyData));
    } catch (error) {
      console.warn('Failed to save skill history to localStorage:', error);
    }
  }
  
  loadFromStorage() {
    try {
      const stored = localStorage.getItem(STORAGE_KEY);
      if (!stored) {
        // No stored data - create a default skill
        this.createNewSkill();
        return;
      }
      
      const historyData = JSON.parse(stored);
      
      if (historyData.version !== HISTORY_VERSION) {
        console.warn('Skill history version mismatch, ignoring stored data');
        this.createNewSkill();
        return;
      }
      
      const snapshot = getStateSnapshot();
      
      // Load skills from storage
      if (historyData.skills && typeof historyData.skills === 'object') {
        // Normalize skill IDs and rebuild skills object
        const normalizedSkills = {};
        const idMapping = {}; // Track old ID -> new ID mappings
        
        Object.entries(historyData.skills).forEach(([oldId, skill]) => {
          const normalizedId = oldId.toLowerCase().trim();
          
          // Normalize the skill object's ID
          if (skill.id) {
            skill.id = skill.id.toLowerCase().trim();
          }
          
          // Use normalized ID as key
          normalizedSkills[normalizedId] = skill;
          
          // Track mapping for updating currentSkillId
          if (oldId !== normalizedId) {
            idMapping[oldId] = normalizedId;
          }
        });
        
        snapshot.skills = normalizedSkills;
        
        // Update currentSkillId if it was normalized
        let currentSkillId = historyData.currentSkillId || null;
        if (currentSkillId && idMapping[currentSkillId]) {
          currentSkillId = idMapping[currentSkillId];
        }
        snapshot.currentSkillId = currentSkillId;
        
        // Rebuild registry from normalized skill IDs
        snapshot.registry = Object.keys(snapshot.skills).map(id => id.toLowerCase());
      }
      
      // If no current skill or it doesn't exist, select first available
      if (!snapshot.currentSkillId || !snapshot.skills[snapshot.currentSkillId]) {
        const skillIds = Object.keys(snapshot.skills);
        if (skillIds.length > 0) {
          snapshot.currentSkillId = skillIds[0];
        } else {
          // No skills at all - create a default one
          this.createNewSkill();
          return;
        }
      }
      
      this.showNotification('Skill history restored from storage', 'info');
      
    } catch (error) {
      console.warn('Failed to load skill history from localStorage:', error);
      // On error, create a default skill
      this.createNewSkill();
    }
  }
  
  destroy() {
    if (this.unsubscribe) {
      this.unsubscribe();
      this.unsubscribe = null;
    }
  }
}

// Initialize the skill history manager
let skillHistoryManager = null;

export function initializeSkillHistory() {
  if (skillHistoryManager) {
    skillHistoryManager.destroy();
  }
  
  skillHistoryManager = new SkillHistoryManager();
  skillHistoryManager.subscribeToState();
  
  return skillHistoryManager;
}

export function getSkillHistoryManager() {
  return skillHistoryManager;
}