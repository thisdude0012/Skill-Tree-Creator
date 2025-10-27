import {
  subscribe,
  getStateSnapshot,
  getCurrentSkill,
  getSkillRegistry,
  updateSkillPartial,
} from "./state.js";

const DOM_SELECTORS = {
  preview: "[data-preview]",
  copyButton: "[data-copy-json]",
  formatToggle: "[data-format-toggle]",
  downloadButton: "[data-download-json]",
  validationSummary: "[data-validation-summary]",
  newSkillButton: "[data-new-skill]",
  exportAllButton: "[data-export-all]",
  generateTreeButton: "[data-generate-tree]",
};

const STATE_KEYS = [
  "id",
  "title", 
  "backgroundTexture",
  "iconTexture", 
  "borderTexture",
  "titleColor",
  "positionX",
  "positionY", 
  "buttonSize",
  "isStartingPoint",
  "description",
  "bonuses",
  "requirements",
  "directConnections",
  "longConnections", 
  "oneWayConnections",
  "tags",
];

const jsonOutputState = {
  isCompact: false,
  lastGeneratedJSON: null,
  lastValidationResult: null,
  sessionSkills: new Map(),
  skillCounter: 1,
  currentTreeName: "",
};

function generateUUID() {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
    const r = Math.random() * 16 | 0;
    const v = c === 'x' ? r : (r & 0x3 | 0x8);
    return v.toString(16);
  });
}

function convertTimerToSeconds(timerString) {
  if (!timerString || typeof timerString !== 'string') {
    return 0;
  }
  
  const parts = timerString.split(':');
  if (parts.length === 2) {
    const minutes = parseInt(parts[0]) || 0;
    const seconds = parseInt(parts[1]) || 0;
    return minutes * 60 + seconds;
  }
  
  // Handle direct seconds input
  const seconds = parseInt(timerString) || 0;
  return seconds;
}

function formatBonus(bonus) {
  if (!bonus || typeof bonus !== 'object') {
    return null;
  }

  const formatted = {
    type: bonus.type || "skilltree:none",
  };

  // Handle different bonus types based on metadata
  if (bonus.amount !== undefined) {
    formatted.amount = parseFloat(bonus.amount) || 0;
  }

  if (bonus.operation !== undefined) {
    formatted.operation = parseInt(bonus.operation) || 0;
  }

  // Attribute bonus specific fields
  if (bonus.type === "skilltree:attribute") {
    if (bonus.attribute) {
      formatted.attribute = bonus.attribute;
    }
    if (bonus.id) {
      formatted.id = bonus.id;
    } else {
      formatted.id = generateUUID();
    }
    if (bonus.name) {
      formatted.name = bonus.name;
    } else {
      formatted.name = "Skill";
    }
  }

  // Effect duration specific fields
  if (bonus.type === "skilltree:effect_duration") {
    if (bonus.effect_type) {
      formatted.effect_type = bonus.effect_type;
    }
    if (bonus.duration) {
      formatted.duration = convertTimerToSeconds(bonus.duration);
    }
    if (bonus.enemy_condition) {
      formatted.enemy_condition = bonus.enemy_condition;
    }
    if (bonus.enemy_multiplier) {
      formatted.enemy_multiplier = bonus.enemy_multiplier;
    }
    if (bonus.player_condition) {
      formatted.player_condition = bonus.player_condition;
    }
    if (bonus.player_multiplier) {
      formatted.player_multiplier = bonus.player_multiplier;
    }
  }

  // Item grant specific fields
  if (bonus.type === "skilltree:grant_item") {
    if (bonus.item) {
      formatted.item = bonus.item;
    }
    if (bonus.count !== undefined) {
      formatted.count = parseInt(bonus.count) || 1;
    }
  }

  // Condition and multiplier structures
  const conditionFields = [
    'player_condition',
    'enemy_multiplier', 
    'player_multiplier',
    'damage_condition',
    'target_condition',
    'attacker_condition'
  ];

  conditionFields.forEach(field => {
    if (bonus[field] && typeof bonus[field] === 'object') {
      formatted[field] = { ...bonus[field] };
      if (!formatted[field].type) {
        formatted[field].type = "skilltree:none";
      }
    } else {
      formatted[field] = { type: "skilltree:none" };
    }
  });

  // Handle other common bonus fields
  const otherFields = [
    'damage_type',
    'equipment_type',
    'experience_source', 
    'loot_type',
    'slot',
    'chance',
    'multiplier',
    'enchantment',
    'stat'
  ];

  otherFields.forEach(field => {
    if (bonus[field] !== undefined && bonus[field] !== "") {
      formatted[field] = bonus[field];
    }
  });

  return formatted;
}

function formatRequirement(requirement) {
  if (!requirement || typeof requirement !== 'object') {
    return null;
  }

  const formatted = {
    type: requirement.type || "skilltree:none",
  };

  // Add requirement-specific fields based on type
  switch (requirement.type) {
    case "skilltree:stat_value":
      if (requirement.stat) formatted.stat = requirement.stat;
      if (requirement.value !== undefined) formatted.value = parseFloat(requirement.value) || 0;
      break;
    case "skilltree:skill_count":
      if (requirement.count !== undefined) formatted.count = parseInt(requirement.count) || 0;
      break;
    case "skilltree:has_enchantment":
      if (requirement.enchantment) formatted.enchantment = requirement.enchantment;
      break;
    case "skilltree:item_condition":
      if (requirement.item) formatted.item = requirement.item;
      break;
  }

  return formatted;
}

function formatDescription(description) {
  if (!Array.isArray(description)) {
    // Convert simple string to description format
    if (description && typeof description === 'string') {
      return [{
        "f_131101_": { "f_131257_": 8092645 },
        "f_131102_": true,
        "f_131106_": false,
        "text": description
      }];
    }
    return [];
  }

  return description.map(desc => {
    if (typeof desc === 'string') {
      return {
        "f_131101_": { "f_131257_": 8092645 },
        "f_131102_": true,
        "f_131106_": false,
        "text": desc
      };
    }
    return desc;
  });
}

function generateSkillJSON(skill) {
  if (!skill || typeof skill !== 'object') {
    console.warn('Invalid skill object provided to generateSkillJSON:', skill);
    return null;
  }

  try {
    const json = {};

    // Copy all basic fields
    STATE_KEYS.forEach(key => {
      if (key === 'bonuses') {
        json[key] = Array.isArray(skill[key]) 
          ? skill[key].map(bonus => formatBonus(bonus)).filter(Boolean)
          : [];
      } else if (key === 'requirements') {
        json[key] = Array.isArray(skill[key])
          ? skill[key].map(req => formatRequirement(req)).filter(Boolean) 
          : [];
      } else if (key === 'description') {
        json[key] = formatDescription(skill[key]);
      } else if (key === 'directConnections' || key === 'longConnections' || key === 'oneWayConnections') {
        json[key] = Array.isArray(skill[key]) ? skill[key] : [];
      } else if (key === 'tags') {
        json[key] = Array.isArray(skill[key]) ? skill[key] : [];
      } else if (key === 'positionX' || key === 'positionY') {
        json[key] = parseFloat(skill[key]) || 0;
      } else if (key === 'buttonSize') {
        json[key] = parseInt(skill[key]) || 16;
      } else if (key === 'isStartingPoint') {
        json[key] = Boolean(skill[key]);
      } else if (skill[key] !== undefined && skill[key] !== null) {
        json[key] = skill[key];
      }
    });

    return json;
  } catch (error) {
    console.error('Error generating skill JSON:', error);
    return null;
  }
}

function validateSkillJSON(json, metadata) {
  const errors = [];
  const warnings = [];

  if (!json) {
    errors.push({ field: 'root', message: 'Invalid skill data' });
    return { valid: false, errors, warnings };
  }

  // Required field validation
  const requiredFields = [
    'id', 'title', 'backgroundTexture', 'iconTexture', 'borderTexture',
    'positionX', 'positionY', 'buttonSize'
  ];

  requiredFields.forEach(field => {
    if (!json[field] || json[field] === '') {
      errors.push({ field, message: `${field} is required` });
    }
  });

  // ID format validation
  if (json.id && typeof json.id === 'string') {
    if (!json.id.match(/^[a-z0-9_.-]+:[a-z0-9_./-]+$/i)) {
      errors.push({ field: 'id', message: 'ID must follow namespace:path format' });
    }
  }

  // Numeric field validation
  ['positionX', 'positionY'].forEach(field => {
    if (json[field] !== undefined && isNaN(parseFloat(json[field]))) {
      errors.push({ field, message: `${field} must be a number` });
    }
  });

  if (json.buttonSize !== undefined && (isNaN(parseInt(json.buttonSize)) || parseInt(json.buttonSize) <= 0)) {
    errors.push({ field: 'buttonSize', message: 'buttonSize must be a positive number' });
  }

  // Title color validation
  if (json.titleColor && !json.titleColor.match(/^[0-9a-f]{6}$/i)) {
    errors.push({ field: 'titleColor', message: 'titleColor must be a 6-digit hex code' });
  }

  // Texture path validation
  ['backgroundTexture', 'iconTexture', 'borderTexture'].forEach(field => {
    if (json[field] && typeof json[field] === 'string' && !json[field].includes(':')) {
      errors.push({ field, message: `${field} must include a namespace` });
    }
  });

  // Bonus validation
  if (Array.isArray(json.bonuses)) {
    json.bonuses.forEach((bonus, index) => {
      if (!bonus || typeof bonus !== 'object') {
        errors.push({ field: `bonuses[${index}]`, message: 'Bonus must be an object' });
        return;
      }

      if (!bonus.type) {
        errors.push({ field: `bonuses[${index}]`, message: 'Bonus type is required' });
      }

      // Validate bonus-specific fields based on type
      if (bonus.type === 'skilltree:attribute') {
        if (!bonus.attribute) {
          errors.push({ field: `bonuses[${index}].attribute`, message: 'Attribute bonus requires an attribute' });
        }
        if (!bonus.id || !bonus.id.match(/^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i)) {
          errors.push({ field: `bonuses[${index}].id`, message: 'Attribute bonus requires a valid UUID' });
        }
      }

      if (bonus.type === 'skilltree:effect_duration') {
        if (!bonus.effect_type) {
          errors.push({ field: `bonuses[${index}].effect_type`, message: 'Effect duration bonus requires an effect type' });
        }
        if (bonus.duration === undefined || isNaN(parseInt(bonus.duration))) {
          errors.push({ field: `bonuses[${index}].duration`, message: 'Effect duration bonus requires a valid duration' });
        }
      }

      if (bonus.type === 'skilltree:grant_item') {
        if (!bonus.item) {
          errors.push({ field: `bonuses[${index}].item`, message: 'Item grant bonus requires an item' });
        }
      }

      // Validate condition structures
      const conditionFields = ['player_condition', 'enemy_multiplier', 'player_multiplier', 'damage_condition', 'target_condition', 'attacker_condition'];
      conditionFields.forEach(conditionField => {
        if (bonus[conditionField] && (!bonus[conditionField].type || typeof bonus[conditionField] !== 'object')) {
          errors.push({ field: `bonuses[${index}].${conditionField}`, message: `${conditionField} must be an object with a type field` });
        }
      });
    });
  }

  // Connection validation
  ['directConnections', 'longConnections', 'oneWayConnections'].forEach(field => {
    if (Array.isArray(json[field])) {
      json[field].forEach((conn, index) => {
        if (typeof conn === 'string' && !conn.match(/^[a-z0-9_.-]+:[a-z0-9_./-]+$/i)) {
          errors.push({ field: `${field}[${index}]`, message: 'Connection must be a valid skill ID' });
        }
      });
      
      // Check for duplicates
      const duplicates = json[field].filter((item, idx) => json[field].indexOf(item) !== idx);
      if (duplicates.length > 0) {
        warnings.push({ field, message: `Duplicate connections found: ${duplicates.join(', ')}` });
      }
    }
  });

  // Requirements validation
  if (Array.isArray(json.requirements)) {
    json.requirements.forEach((req, index) => {
      if (!req || typeof req !== 'object') {
        errors.push({ field: `requirements[${index}]`, message: 'Requirement must be an object' });
        return;
      }

      if (!req.type) {
        errors.push({ field: `requirements[${index}]`, message: 'Requirement type is required' });
      }

      // Validate requirement-specific fields
      if (req.type === 'skilltree:stat_value') {
        if (!req.stat) {
          errors.push({ field: `requirements[${index}].stat`, message: 'Stat value requirement requires a stat' });
        }
        if (req.value === undefined || isNaN(parseFloat(req.value))) {
          errors.push({ field: `requirements[${index}].value`, message: 'Stat value requirement requires a valid value' });
        }
      }
    });
  }

  return {
    valid: errors.length === 0,
    errors,
    warnings
  };
}

function formatJSONForDisplay(json, compact = false) {
  if (!json) return '';
  
  try {
    return compact 
      ? JSON.stringify(json)
      : JSON.stringify(json, null, 2);
  } catch (error) {
    return '// Error formatting JSON: ' + error.message;
  }
}

function escapeHtml(text) {
  const div = document.createElement('div');
  div.textContent = text;
  return div.innerHTML;
}

function applySyntaxHighlighting(jsonString) {
  if (!jsonString) return '';
  
  return jsonString
    .replace(/("(\\u[a-zA-Z0-9]{4}|\\[^u]|[^\\"])*"(\s*:)?|\b(true|false|null)\b|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?)/g, function (match) {
      let cls = 'json-number';
      if (/^"/.test(match)) {
        if (/:$/.test(match)) {
          cls = 'json-key';
        } else {
          cls = 'json-string';
        }
      } else if (/true|false/.test(match)) {
        cls = 'json-boolean';
      } else if (/null/.test(match)) {
        cls = 'json-null';
      }
      return '<span class="' + cls + '">' + escapeHtml(match) + '</span>';
    });
}

function updatePreview() {
  const previewElement = document.querySelector(DOM_SELECTORS.preview);
  if (!previewElement) {
    console.warn('Preview element not found');
    return;
  }

  const currentSkill = getCurrentSkill();
  if (!currentSkill) {
    previewElement.innerHTML = '<div class="preview-empty">No skill data to preview</div>';
    return;
  }

  const snapshot = getStateSnapshot();
  const metadata = snapshot.metadata;
  
  // Generate JSON
  const json = generateSkillJSON(currentSkill);
  jsonOutputState.lastGeneratedJSON = json;

  // Validate
  const validation = validateSkillJSON(json, metadata);
  jsonOutputState.lastValidationResult = validation;

  // Format for display
  const formatted = formatJSONForDisplay(json, jsonOutputState.isCompact);
  const highlighted = applySyntaxHighlighting(formatted);

  // Create preview HTML
  let previewHTML = '';

  // Validation summary
  if (validation.valid) {
    previewHTML += '<div class="preview-validation preview-validation--valid">✅ Skill is valid and ready to export</div>';
    if (validation.warnings && validation.warnings.length > 0) {
      previewHTML += '<div class="preview-warnings">';
      validation.warnings.forEach(warning => {
        previewHTML += `<div class="preview-warning">⚠️ ${warning.message}</div>`;
      });
      previewHTML += '</div>';
    }
  } else {
    previewHTML += `<div class="preview-validation preview-validation--invalid">❌ ${validation.errors.length} error${validation.errors.length !== 1 ? 's' : ''} found</div>`;
    if (validation.errors.length > 0) {
      previewHTML += '<div class="preview-errors">';
      validation.errors.forEach(error => {
        previewHTML += `<div class="preview-error" data-field="${error.field}">• ${error.message}</div>`;
      });
      previewHTML += '</div>';
    }
  }

  // Controls
  previewHTML += `
    <div class="preview-controls">
      <button class="preview-button" data-copy-json ${!validation.valid ? 'disabled' : ''}>
        📋 Copy to Clipboard
      </button>
      <button class="preview-button" data-format-toggle>
        ${jsonOutputState.isCompact ? '📖 Pretty Print' : '📦 Compact'}
      </button>
      <button class="preview-button" data-download-json ${!validation.valid ? 'disabled' : ''}>
        💾 Download JSON
      </button>
    </div>
  `;

  // JSON display
  previewHTML += `
    <div class="preview-json-container">
      <pre class="preview-json"><code>${highlighted}</code></pre>
    </div>
  `;

  previewElement.innerHTML = previewHTML;

  // Add event listeners
  setupPreviewControls();

  // Add click handlers for validation errors
  const errorElements = previewElement.querySelectorAll('.preview-error');
  errorElements.forEach(errorElement => {
    errorElement.addEventListener('click', () => {
      const field = errorElement.dataset.field;
      jumpToField(field);
    });
  });
}

function jumpToField(field) {
  // Parse field path like "bonuses[2].attribute" or "title"
  let targetField = field;
  let targetBonusIndex = null;
  
  const bonusMatch = field.match(/^bonuses\[(\d+)\]\.(.+)$/);
  if (bonusMatch) {
    targetBonusIndex = parseInt(bonusMatch[1]);
    targetField = bonusMatch[2];
  }

  // Find the corresponding form field or bonus card
  if (targetBonusIndex !== null) {
    // Focus on the specific bonus card
    const bonusCards = document.querySelectorAll('[data-bonus-card]');
    if (bonusCards[targetBonusIndex]) {
      bonusCards[targetBonusIndex].scrollIntoView({ behavior: 'smooth', block: 'center' });
      bonusCards[targetBonusIndex].classList.add('highlight-error');
      setTimeout(() => {
        bonusCards[targetBonusIndex].classList.remove('highlight-error');
      }, 3000);
    }
  } else {
    // Focus on regular form field
    const fieldWrapper = document.querySelector(`[data-field-wrapper="${targetField}"]`);
    if (fieldWrapper) {
      fieldWrapper.scrollIntoView({ behavior: 'smooth', block: 'center' });
      const input = fieldWrapper.querySelector('input, select, textarea');
      if (input) {
        input.focus();
        fieldWrapper.classList.add('highlight-error');
        setTimeout(() => {
          fieldWrapper.classList.remove('highlight-error');
        }, 3000);
      }
    }
  }
}

function setupPreviewControls() {
  const copyButton = document.querySelector(DOM_SELECTORS.copyButton);
  const formatToggle = document.querySelector(DOM_SELECTORS.formatToggle);
  const downloadButton = document.querySelector(DOM_SELECTORS.downloadButton);

  if (copyButton) {
    copyButton.onclick = () => copyJSONToClipboard();
  }

  if (formatToggle) {
    formatToggle.onclick = () => toggleFormat();
  }

  if (downloadButton) {
    downloadButton.onclick = () => downloadJSON();
  }
}

function copyJSONToClipboard() {
  if (!jsonOutputState.lastGeneratedJSON) return;

  const jsonString = JSON.stringify(jsonOutputState.lastGeneratedJSON, null, 2);
  
  navigator.clipboard.writeText(jsonString).then(() => {
    // Show success feedback
    const button = document.querySelector(DOM_SELECTORS.copyButton);
    if (button) {
      const originalText = button.textContent;
      button.textContent = '✅ Copied!';
      setTimeout(() => {
        button.textContent = originalText;
      }, 2000);
    }
  }).catch(err => {
    console.error('Failed to copy JSON:', err);
  });
}

function toggleFormat() {
  jsonOutputState.isCompact = !jsonOutputState.isCompact;
  updatePreview();
}

function downloadJSON() {
  if (!jsonOutputState.lastGeneratedJSON) return;

  const currentSkill = getCurrentSkill();
  if (!currentSkill || !currentSkill.id) return;

  const filename = currentSkill.id.replace('skilltree:', '') + '.json';
  const jsonString = JSON.stringify(jsonOutputState.lastGeneratedJSON, null, 2);

  const blob = new Blob([jsonString], { type: 'application/json' });
  const url = URL.createObjectURL(blob);
  
  const a = document.createElement('a');
  a.href = url;
  a.download = filename;
  document.body.appendChild(a);
  a.click();
  document.body.removeChild(a);
  URL.revokeObjectURL(url);
}

function updateSessionInfo() {
  const countElement = document.querySelector('[data-session-count]');
  const treeElement = document.querySelector('[data-session-tree]');
  const savedElement = document.querySelector('[data-session-saved]');

  if (countElement) {
    countElement.textContent = jsonOutputState.sessionSkills.size;
  }

  if (treeElement) {
    treeElement.textContent = jsonOutputState.currentTreeName || 'None';
  }

  if (savedElement) {
    try {
      const saved = localStorage.getItem('skillCreatorSession');
      if (saved) {
        const sessionData = JSON.parse(saved);
        if (sessionData.lastSaved) {
          const lastSaved = new Date(sessionData.lastSaved);
          savedElement.textContent = lastSaved.toLocaleString();
        } else {
          savedElement.textContent = 'Never';
        }
      } else {
        savedElement.textContent = 'Never';
      }
    } catch (error) {
      savedElement.textContent = 'Unknown';
    }
  }
}

function saveToLocalStorage() {
  try {
    const sessionData = {
      skills: Array.from(jsonOutputState.sessionSkills.values()),
      currentTreeName: jsonOutputState.currentTreeName,
      skillCounter: jsonOutputState.skillCounter,
      lastSaved: new Date().toISOString(),
    };
    localStorage.setItem('skillCreatorSession', JSON.stringify(sessionData));
  } catch (error) {
    console.warn('Failed to save session to localStorage:', error);
  }
}

function loadFromLocalStorage() {
  try {
    const saved = localStorage.getItem('skillCreatorSession');
    if (saved) {
      const sessionData = JSON.parse(saved);
      if (sessionData.skills && Array.isArray(sessionData.skills)) {
        sessionData.skills.forEach(skill => {
          jsonOutputState.sessionSkills.set(skill.id, skill);
        });
      }
      if (sessionData.currentTreeName) {
        jsonOutputState.currentTreeName = sessionData.currentTreeName;
      }
      if (sessionData.skillCounter) {
        jsonOutputState.skillCounter = sessionData.skillCounter;
      }
    }
  } catch (error) {
    console.warn('Failed to load session from localStorage:', error);
  }
}

function createNewSkill() {
  const currentSkill = getCurrentSkill();
  if (currentSkill) {
    jsonOutputState.sessionSkills.set(currentSkill.id, currentSkill);
  }

  const treeName = currentSkill?.skillTreeName || jsonOutputState.currentTreeName || 'untitled';
  jsonOutputState.currentTreeName = treeName;
  jsonOutputState.skillCounter++;
  
  const newId = `skilltree:${treeName}_${jsonOutputState.skillCounter}`;

  // Create new default skill
  const newSkill = {
    skillTreeName: treeName,
    id: newId,
    bonuses: [],
    requirements: [],
    directConnections: [],
    longConnections: [],
    oneWayConnections: [],
    tags: [],
    backgroundTexture: "skilltree:textures/icons/background/lesser.png",
    iconTexture: "skilltree:textures/icons/void.png", 
    borderTexture: "skilltree:textures/tooltip/lesser.png",
    title: "New Skill",
    titleColor: "",
    positionX: 0,
    positionY: 0,
    buttonSize: 16,
    isStartingPoint: false,
    description: [],
  };

  // Update state with new skill
  updateSkillPartial(newSkill);
  updateSessionInfo();
  saveToLocalStorage();
}

function exportAllSkills() {
  if (jsonOutputState.sessionSkills.size === 0) {
    alert('No skills to export. Create some skills first!');
    return;
  }

  const skills = Array.from(jsonOutputState.sessionSkills.values());
  
  skills.forEach((skill, index) => {
    setTimeout(() => {
      const json = generateSkillJSON(skill);
      const filename = skill.id.replace('skilltree:', '') + '.json';
      const jsonString = JSON.stringify(json, null, 2);

      const blob = new Blob([jsonString], { type: 'application/json' });
      const url = URL.createObjectURL(blob);
      
      const a = document.createElement('a');
      a.href = url;
      a.download = filename;
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      URL.revokeObjectURL(url);
    }, index * 100); // Small delay to avoid browser blocking multiple downloads
  });
}

function clearSession() {
  if (!confirm('Are you sure you want to start fresh? This will clear all skills from the current session and cannot be undone.')) {
    return;
  }

  // Clear session data
  jsonOutputState.sessionSkills.clear();
  jsonOutputState.skillCounter = 1;
  jsonOutputState.currentTreeName = '';

  // Clear localStorage
  try {
    localStorage.removeItem('skillCreatorSession');
  } catch (error) {
    console.warn('Failed to clear localStorage:', error);
  }

  // Update UI
  updateSessionInfo();
  
  // Create a new default skill
  const newSkill = {
    skillTreeName: '',
    id: 'skilltree:untitled_skill',
    bonuses: [],
    requirements: [],
    directConnections: [],
    longConnections: [],
    oneWayConnections: [],
    tags: [],
    backgroundTexture: "skilltree:textures/icons/background/lesser.png",
    iconTexture: "skilltree:textures/icons/void.png", 
    borderTexture: "skilltree:textures/tooltip/lesser.png",
    title: "Untitled Skill",
    titleColor: "",
    positionX: 0,
    positionY: 0,
    buttonSize: 16,
    isStartingPoint: false,
    description: [],
  };

  updateSkillPartial(newSkill);
}

function generateSkillTreeJSON() {
  if (jsonOutputState.sessionSkills.size === 0) {
    alert('No skills to include in skill tree. Create some skills first!');
    return;
  }

  const treeName = prompt('Enter skill tree name (e.g., "warrior", "mage"):', jsonOutputState.currentTreeName || 'skilltree');
  if (!treeName) return;

  const skillIds = Array.from(jsonOutputState.sessionSkills.keys());
  const skillTreeJSON = {
    skills: skillIds
  };

  const filename = `${treeName}.json`;
  const jsonString = JSON.stringify(skillTreeJSON, null, 2);

  const blob = new Blob([jsonString], { type: 'application/json' });
  const url = URL.createObjectURL(blob);
  
  const a = document.createElement('a');
  a.href = url;
  a.download = filename;
  document.body.appendChild(a);
  a.click();
  document.body.removeChild(a);
  URL.revokeObjectURL(url);
}

let debounceTimer = null;
function debouncedUpdatePreview() {
  if (debounceTimer) {
    clearTimeout(debounceTimer);
  }
  debounceTimer = setTimeout(updatePreview, 300);
}

function initializeJSONOutput() {
  // Load saved session from localStorage
  loadFromLocalStorage();

  // Subscribe to state changes
  subscribe((snapshot) => {
    if (snapshot.ready && snapshot.currentSkill) {
      // Update current tree name from skill
      if (snapshot.currentSkill.skillTreeName) {
        jsonOutputState.currentTreeName = snapshot.currentSkill.skillTreeName;
      }
      
      debouncedUpdatePreview();
      updateSessionInfo();
      
      // Auto-save on changes
      saveToLocalStorage();
    }
  });

  // Set up global event listeners
  document.addEventListener('click', (event) => {
    if (event.target.matches(DOM_SELECTORS.newSkillButton)) {
      createNewSkill();
    } else if (event.target.matches(DOM_SELECTORS.exportAllButton)) {
      exportAllSkills();
    } else if (event.target.matches(DOM_SELECTORS.generateTreeButton)) {
      generateSkillTreeJSON();
    } else if (event.target.matches('[data-clear-session]')) {
      clearSession();
    }
  });

  // Also handle copy/download buttons in exports section
  document.addEventListener('click', (event) => {
    if (event.target.matches('[data-copy-json]')) {
      copyJSONToClipboard();
    } else if (event.target.matches('[data-download-json]')) {
      downloadJSON();
    }
  });

  // Keyboard shortcuts
  document.addEventListener('keydown', (event) => {
    // Ctrl+S / Cmd+S - Download current skill
    if ((event.ctrlKey || event.metaKey) && event.key === 's') {
      event.preventDefault();
      if (jsonOutputState.lastGeneratedJSON && jsonOutputState.lastValidationResult?.valid) {
        downloadJSON();
      }
    }
    
    // Ctrl+C - Copy JSON (when not in input field)
    if ((event.ctrlKey || event.metaKey) && event.key === 'c' && !event.target.matches('input, textarea')) {
      event.preventDefault();
      copyJSONToClipboard();
    }
    
    // Ctrl+N - New skill
    if ((event.ctrlKey || event.metaKey) && event.key === 'n') {
      event.preventDefault();
      createNewSkill();
    }
  });

  // Initial preview update
  setTimeout(() => {
    updatePreview();
    updateSessionInfo();
  }, 100);
}

export {
  initializeJSONOutput,
  generateSkillJSON,
  validateSkillJSON,
  updatePreview,
  updateSessionInfo,
  saveToLocalStorage,
  loadFromLocalStorage,
  clearSession,
  jsonOutputState,
};