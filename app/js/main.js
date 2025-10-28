import {
  initializeState,
  subscribe,
  updateSkillField,
  getStateSnapshot,
} from "./state.js";
import { initializeBonusBuilder } from "./bonusBuilder.js";
import {
  initializeTextureDropdowns,
  updateTexturePreview,
} from "./textureManager.js";
import { initializeJSONOutput } from "./jsonOutput.js";
import { initializeSkillHistory } from "./skillHistoryManager.js";

const dom = {
  root: document.documentElement,
  navToggle: document.querySelector("[data-nav-toggle]"),
  nav: document.querySelector("[data-nav]"),
  navLinks: [],
  sections: Array.from(document.querySelectorAll("[data-section]")),
  preview: document.querySelector("[data-preview]"),
  placeholders: Array.from(document.querySelectorAll("[data-placeholder]")),
  skillForm: document.querySelector("[data-skill-form]"),
  validationSummary: document.querySelector("[data-validation-summary]"),
};

dom.navLinks = dom.nav ? Array.from(dom.nav.querySelectorAll("[data-nav-link]")) : [];
dom.fieldInputs = new Map();
dom.fieldWrappers = new Map();
dom.fieldFeedback = new Map();

let skillCounter = 1;
let currentSkillTree = "";
let lastGeneratedID = "";

dom.root.classList.add("theme-dark");

dom.sections.forEach((section) => {
  section.dataset.ready = "false";
});

const uiState = {
  activeSectionId: dom.sections[0] ? dom.sections[0].id : null,
  navOpen: false,
};

function setActiveSection(sectionId) {
  if (!sectionId) return;
  const section = dom.sections.find((entry) => entry.id === sectionId);
  if (!section) return;

  uiState.activeSectionId = sectionId;
  dom.sections.forEach((entry) => {
    entry.dataset.ready = entry.id === sectionId ? "true" : entry.dataset.ready;
  });

  dom.navLinks.forEach((link) => {
    const href = link.getAttribute("href") || "";
    const targetId = href.startsWith("#") ? href.slice(1) : href;
    const isActive = targetId === sectionId;
    link.classList.toggle("is-active", isActive);
    link.setAttribute("aria-current", isActive ? "location" : "false");
  });
}

function closeNav() {
  if (!dom.nav || !dom.navToggle) return;
  dom.nav.classList.remove("is-open");
  dom.navToggle.setAttribute("aria-expanded", "false");
  uiState.navOpen = false;
}

function openNav() {
  if (!dom.nav || !dom.navToggle) return;
  dom.nav.classList.add("is-open");
  dom.navToggle.setAttribute("aria-expanded", "true");
  uiState.navOpen = true;
}

function toggleNav() {
  if (!dom.nav || !dom.navToggle) return;
  if (uiState.navOpen) {
    closeNav();
  } else {
    openNav();
  }
}

function setupNav() {
  if (!dom.nav || !dom.navToggle) return;
  dom.navToggle.addEventListener("click", toggleNav);

  dom.navLinks.forEach((link) => {
    link.addEventListener("click", () => {
      if (window.innerWidth < 768) {
        closeNav();
      }
    });
  });

  if (window.innerWidth >= 768) {
    openNav();
  } else {
    closeNav();
  }

  window.addEventListener("resize", () => {
    if (window.innerWidth >= 768) {
      openNav();
    } else {
      closeNav();
    }
  });

  document.addEventListener("keydown", (event) => {
    if (event.key === "Escape" && uiState.navOpen && window.innerWidth < 768) {
      closeNav();
    }
  });
}

function observeSections() {
  if (!("IntersectionObserver" in window) || dom.sections.length === 0) {
    return;
  }

  const observer = new IntersectionObserver(
    (entries) => {
      const visible = entries
        .filter((entry) => entry.isIntersecting)
        .sort((a, b) => a.target.offsetTop - b.target.offsetTop);

      if (visible.length > 0) {
        const topMost = visible[0];
        setActiveSection(topMost.target.id);
      }
    },
    {
      root: null,
      rootMargin: "-45% 0px -45%",
      threshold: [0.25, 0.6, 1],
    },
  );

  dom.sections.forEach((section) => observer.observe(section));
}

function hydratePlaceholders() {
  dom.placeholders.forEach((element) => {
    element.dataset.hydrated = "false";
  });
}

function generateSkillID() {
  if (!currentSkillTree || currentSkillTree.trim() === "") {
    const id = `skilltree:untitled_${skillCounter}`;
    lastGeneratedID = id;
    return id;
  }
  const sanitized = currentSkillTree.trim().toLowerCase().replace(/[^a-z0-9_]/g, "_");
  const id = `skilltree:${sanitized}_${skillCounter}`;
  lastGeneratedID = id;
  return id;
}

function setupSkillForm() {
  if (!dom.skillForm) return;
  const inputs = Array.from(dom.skillForm.querySelectorAll("[data-field]"));

  inputs.forEach((input) => {
    const field = input.dataset.field;
    if (!field) return;
    
    if (input.matches(".texture-select")) {
      dom.fieldInputs.set(field, input);
    } else if (!input.matches(".texture-manual-input")) {
      dom.fieldInputs.set(field, input);
    }
    
    const wrapper = input.closest("[data-field-wrapper]");
    if (wrapper) {
      dom.fieldWrappers.set(field, wrapper);
      const feedback = wrapper.querySelector("[data-feedback]");
      if (feedback) {
        dom.fieldFeedback.set(field, feedback);
      }
    }

    if (field === "skillTreeName") {
      input.addEventListener("input", (event) => {
        currentSkillTree = event.target.value;
        skillCounter = 1;
        const newID = generateSkillID();
        updateSkillField("id", newID);
        
        const idInput = dom.fieldInputs.get("id");
        if (idInput && idInput.value !== newID) {
          idInput.value = newID;
        }
      });
      return;
    }

    const eventName = input.type === "checkbox" ? "change" : "input";
    input.addEventListener(eventName, (event) => {
      const target = event.currentTarget;
      const { field: fieldName } = target.dataset;
      if (!fieldName) return;
      const value = target.type === "checkbox" ? target.checked : target.value;
      
      if (fieldName === "id") {
        if (value === lastGeneratedID) {
          skillCounter++;
        }
      }
      
      updateSkillField(fieldName, value);
      
      if (fieldName === "backgroundTexture" || fieldName === "iconTexture" || fieldName === "borderTexture") {
        updateTexturePreview(fieldName, value);
      }
    });
  });
  
  initializeTextureDropdowns();
}

function setFormDisabled(disabled) {
  dom.fieldInputs.forEach((input) => {
    if (disabled) {
      input.setAttribute("disabled", "true");
    } else {
      input.removeAttribute("disabled");
    }
  });
}

function formatFieldValue(field, value) {
  if (value === undefined || value === null) {
    return "";
  }
  if (field === "description") {
    if (Array.isArray(value)) {
      // Extract text from description array format
      return value.map(item => item?.text || "").join("\n");
    }
    if (typeof value === "string") {
      return value;
    }
    return "";
  }
  if (field === "tags") {
    if (Array.isArray(value)) {
      return value.join(", ");
    }
    return String(value);
  }
  if (Array.isArray(value)) {
    return value.join(", ");
  }
  if (typeof value === "number") {
    return Number.isFinite(value) ? String(value) : "";
  }
  return String(value);
}

function renderSkillForm(skill) {
  if (!skill) {
    dom.fieldInputs.forEach((input) => {
      if (input.type === "checkbox") {
        input.checked = false;
      } else {
        input.value = "";
      }
    });
    updateTexturePreview("backgroundTexture", "");
    updateTexturePreview("iconTexture", "");
    updateTexturePreview("borderTexture", "");
    return;
  }

  dom.fieldInputs.forEach((input, field) => {
    if (!(field in skill)) {
      if (input.type === "checkbox") {
        input.checked = false;
      } else if (input.value !== "") {
        input.value = "";
      }
      return;
    }

    const value = skill[field];
    if (input.type === "checkbox") {
      const nextChecked = Boolean(value);
      if (input.checked !== nextChecked) {
        input.checked = nextChecked;
      }
      return;
    }

    const formatted = formatFieldValue(field, value);
    if (input.value !== formatted) {
      input.value = formatted;
    }
    
    if (field === "backgroundTexture" || field === "iconTexture" || field === "borderTexture") {
      updateTexturePreview(field, formatted);
    }
  });
}

function applyFieldValidation(field, status, skill) {
  const wrapper = dom.fieldWrappers.get(field);
  const feedback = dom.fieldFeedback.get(field);
  if (!wrapper) return;

  const isValid = !status || status.valid;
  let hasValue = false;
  if (skill && field in skill) {
    const fieldValue = skill[field];
    if (Array.isArray(fieldValue)) {
      hasValue = fieldValue.length > 0;
    } else if (typeof fieldValue === "string") {
      hasValue = fieldValue.trim() !== "";
    } else if (typeof fieldValue === "boolean") {
      hasValue = true;
    } else if (fieldValue !== null && fieldValue !== undefined) {
      hasValue = true;
    }
  }

  wrapper.classList.toggle("form-field--error", !isValid);
  wrapper.classList.toggle("form-field--valid", isValid && hasValue);

  if (feedback) {
    feedback.textContent = !isValid && status ? status.message : "";
  }
}

function clearValidationState() {
  dom.fieldWrappers.forEach((wrapper) => {
    wrapper.classList.remove("form-field--error", "form-field--valid");
  });
  dom.fieldFeedback.forEach((feedback) => {
    feedback.textContent = "";
  });
}

function renderValidation(validation, skill) {
  if (!validation || !validation.fields) {
    clearValidationState();
    return;
  }

  dom.fieldWrappers.forEach((_, field) => {
    const status = validation.fields[field];
    applyFieldValidation(field, status, skill);
  });
}

function renderSummary(snapshot) {
  if (!dom.validationSummary) return;

  const { loading, ready, error, validation } = snapshot;
  const summary = dom.validationSummary;

  if (error) {
    summary.dataset.state = "error";
    summary.textContent = `Failed to load metadata: ${error}`;
    return;
  }

  if (loading && !ready) {
    summary.dataset.state = "loading";
    summary.textContent = "Loading Passive Skill Tree metadata…";
    return;
  }

  if (!ready) {
    summary.dataset.state = "idle";
    summary.textContent = "Preparing form state…";
    return;
  }

  if (validation && validation.valid) {
    summary.dataset.state = "valid";
    summary.textContent = "Skill configuration is valid and matches the mod format.";
  } else {
    summary.dataset.state = "invalid";
    summary.textContent = "Resolve highlighted fields to match the Passive Skill Tree format.";
  }
}

function renderState(snapshot) {
  const { ready, loading, currentSkill, validation } = snapshot;

  setFormDisabled(!ready || loading);
  renderSkillForm(currentSkill);
  renderValidation(validation, currentSkill);
  renderSummary(snapshot);
}

function syncHashWithActiveSection() {
  const hash = window.location.hash.replace("#", "");
  if (!hash) {
    setActiveSection(uiState.activeSectionId);
    return;
  }

  const section = dom.sections.find((entry) => entry.id === hash);
  if (section) {
    setActiveSection(section.id);
    section.scrollIntoView({ behavior: "smooth", block: "start" });
  }
}

setupNav();
observeSections();
hydratePlaceholders();
setupSkillForm();
syncHashWithActiveSection();

window.addEventListener("hashchange", () => {
  syncHashWithActiveSection();
  if (uiState.navOpen && window.innerWidth < 768) {
    closeNav();
  }
});

const unsubscribe = subscribe(renderState);
const bonusBuilder = initializeBonusBuilder();
initializeJSONOutput();
const skillHistoryManager = initializeSkillHistory();
renderState(getStateSnapshot());

initializeState().catch((error) => {
  console.error("State initialization failed", error);
});

export { dom, uiState as state, setActiveSection };

window.SkillCreatorShell = {
  dom,
  state: uiState,
  setActiveSection,
  toggleNav,
  unsubscribe,
  bonusBuilder,
};
