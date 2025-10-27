import {
  isNamespacePath,
  validateSkill,
  validateSkillField,
} from "./validation.js";

const DATA_SOURCES = {
  metadata: "../data/metadata.json",
  attributes: "../data/minecraft-attributes.json",
  enchantments: "../data/minecraft-enchantments.json",
  stats: "../data/minecraft-stats.json",
  custom: "../data/custom-entries.json",
  items: "../data/minecraft-items.json",
  effects: "../data/minecraft-effects.json",
};

const watchers = new Set();

const state = {
  ready: false,
  loading: false,
  error: null,
  metadata: null,
  references: {
    attributes: [],
    enchantments: [],
    stats: [],
    items: [],
    effects: [],
  },
  customEntries: {
    attributes: [],
    enchantments: [],
    stats: {},
  },
  skills: {},
  currentSkillId: null,
  registry: new Set(),
  validation: {
    fields: {},
    valid: false,
  },
};

function toLabel(id) {
  if (typeof id !== "string") return "";
  return id
    .split(":").pop()
    .split(/[\/_-]+/)
    .filter(Boolean)
    .map((part) => part.charAt(0).toUpperCase() + part.slice(1))
    .join(" ");
}

async function loadJSON(relativePath) {
  const url = new URL(relativePath, import.meta.url);
  let response;
  try {
    response = await fetch(url);
  } catch (error) {
    const isFileProtocol =
      typeof window !== "undefined" && window.location?.protocol === "file:";
    if (isFileProtocol) {
      throw new Error(
        `Failed to load ${relativePath}. The Skill Creator must be served over HTTP to avoid browser security restrictions.`,
      );
    }
    const errorMessage = error instanceof Error ? error.message : String(error);
    throw new Error(`Network request failed for ${relativePath}: ${errorMessage}`);
  }
  if (!response.ok) {
    const details = response.statusText ? ` ${response.statusText}` : "";
    throw new Error(`Failed to load ${relativePath}: ${response.status}${details}`);
  }
  return response.json();
}

function cloneValue(value) {
  if (typeof structuredClone === "function") {
    try {
      return structuredClone(value);
    } catch {
      // Fallback to JSON clone below
    }
  }
  if (value === undefined || value === null) {
    return value;
  }
  return JSON.parse(JSON.stringify(value));
}

function getActiveSkill() {
  if (!state.currentSkillId) {
    return null;
  }
  return state.skills[state.currentSkillId] || null;
}

function ensureBonusesArray(skill) {
  if (!skill) {
    return [];
  }
  if (!Array.isArray(skill.bonuses)) {
    skill.bonuses = [];
  }
  return skill.bonuses;
}

function notifyWatchers() {
  const snapshot = getStateSnapshot();
  watchers.forEach((listener) => {
    try {
      listener(snapshot);
    } catch (error) {
      console.error("State watcher threw", error);
    }
  });
}

function getStateSnapshot() {
  const currentSkill = state.currentSkillId ? state.skills[state.currentSkillId] : null;
  return {
    ready: state.ready,
    loading: state.loading,
    error: state.error,
    metadata: state.metadata,
    references: state.references,
    customEntries: state.customEntries,
    skills: state.skills,
    currentSkillId: state.currentSkillId,
    currentSkill,
    registry: Array.from(state.registry),
    validation: state.validation,
  };
}

function subscribe(listener) {
  if (typeof listener !== "function") {
    throw new TypeError("Subscription callback must be a function");
  }
  watchers.add(listener);
  return () => {
    watchers.delete(listener);
  };
}

function createDefaultSkill() {
  return {
    id: "skilltree:untitled_skill",
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
}

function normalizeAttributeEntry(entry, category = "generic") {
  if (typeof entry === "string") {
    const trimmed = entry.trim().toLowerCase();
    if (!isNamespacePath(trimmed)) return null;
    return {
      id: trimmed,
      name: toLabel(trimmed),
      category,
    };
  }
  if (!entry || typeof entry !== "object") return null;
  const id = typeof entry.id === "string" ? entry.id.trim().toLowerCase() : null;
  if (!id || !isNamespacePath(id)) return null;
  return {
    id,
    name: entry.name || toLabel(id),
    category: entry.category || category,
    description: entry.description || "",
  };
}

function normalizeEnchantmentEntry(entry, category = "general") {
  if (typeof entry === "string") {
    const trimmed = entry.trim().toLowerCase();
    if (!isNamespacePath(trimmed)) return null;
    return {
      id: trimmed,
      name: toLabel(trimmed),
      category,
    };
  }
  if (!entry || typeof entry !== "object") return null;
  const id = typeof entry.id === "string" ? entry.id.trim().toLowerCase() : null;
  if (!id || !isNamespacePath(id)) return null;
  return {
    id,
    name: entry.name || toLabel(id),
    category: entry.category || category,
    description: entry.description || "",
  };
}

function normalizeStatEntry(entry) {
  if (typeof entry === "string") {
    const trimmed = entry.trim().toLowerCase();
    if (!isNamespacePath(trimmed)) return null;
    return {
      id: trimmed,
      name: toLabel(trimmed),
    };
  }
  if (!entry || typeof entry !== "object") return null;
  const id = typeof entry.id === "string" ? entry.id.trim().toLowerCase() : null;
  if (!id || !isNamespacePath(id)) return null;
  return {
    id,
    name: entry.name || toLabel(id),
    description: entry.description || "",
  };
}

function normalizeItemEntry(entry) {
  if (typeof entry === "string") {
    const trimmed = entry.trim().toLowerCase();
    if (!isNamespacePath(trimmed)) return null;
    return {
      id: trimmed,
      name: toLabel(trimmed),
      category: "",
    };
  }
  if (!entry || typeof entry !== "object") return null;
  const id = typeof entry.id === "string" ? entry.id.trim().toLowerCase() : null;
  if (!id || !isNamespacePath(id)) return null;
  const displayName =
    (typeof entry.display === "string" && entry.display.trim()) ||
    (typeof entry.name === "string" && entry.name.trim()) ||
    toLabel(id);
  const category = typeof entry.category === "string" ? entry.category.trim() : "";
  return {
    id,
    name: displayName,
    category,
  };
}

function normalizeEffectEntry(entry) {
  if (typeof entry === "string") {
    const trimmed = entry.trim().toLowerCase();
    if (!isNamespacePath(trimmed)) return null;
    return {
      id: trimmed,
      name: toLabel(trimmed),
      category: "",
    };
  }
  if (!entry || typeof entry !== "object") return null;
  const id = typeof entry.id === "string" ? entry.id.trim().toLowerCase() : null;
  if (!id || !isNamespacePath(id)) return null;
  const displayName =
    (typeof entry.display === "string" && entry.display.trim()) ||
    (typeof entry.name === "string" && entry.name.trim()) ||
    toLabel(id);
  const category = typeof entry.category === "string" ? entry.category.trim() : "";
  return {
    id,
    name: displayName,
    category,
  };
}

function buildItemReferences(entries = []) {
  const map = new Map();
  entries.forEach((entry) => {
    const normalized = normalizeItemEntry(entry);
    if (normalized) {
      map.set(normalized.id, normalized);
    }
  });
  return Array.from(map.values()).sort((a, b) => normalizedLabelSort(a.name, b.name));
}

function buildEffectReferences(entries = []) {
  const map = new Map();
  entries.forEach((entry) => {
    const normalized = normalizeEffectEntry(entry);
    if (normalized) {
      map.set(normalized.id, normalized);
    }
  });
  return Array.from(map.values()).sort((a, b) => normalizedLabelSort(a.name, b.name));
}

function normalizedLabelSort(a, b) {
  const labelA = a || "";
  const labelB = b || "";
  return labelA.localeCompare(labelB, undefined, { sensitivity: "base" });
}

function mergeAttributeReferences(vanilla = [], custom = []) {
  const entries = new Map();
  vanilla.forEach((entry) => {
    const normalized = normalizeAttributeEntry(entry, entry.category);
    if (normalized) entries.set(normalized.id, normalized);
  });
  custom.forEach((entry) => {
    const normalized = normalizeAttributeEntry(entry, "custom");
    if (normalized) entries.set(normalized.id, normalized);
  });
  return Array.from(entries.values()).sort((a, b) => a.id.localeCompare(b.id));
}

function mergeEnchantmentReferences(vanilla = [], custom = []) {
  const entries = new Map();
  vanilla.forEach((entry) => {
    const normalized = normalizeEnchantmentEntry(entry, entry.category);
    if (normalized) entries.set(normalized.id, normalized);
  });
  custom.forEach((entry) => {
    const normalized = normalizeEnchantmentEntry(entry, "custom");
    if (normalized) entries.set(normalized.id, normalized);
  });
  return Array.from(entries.values()).sort((a, b) => a.id.localeCompare(b.id));
}

function mergeStatReferences(vanillaCategories = [], custom = {}) {
  const byCategory = new Map();
  vanillaCategories.forEach((category) => {
    if (!category || typeof category !== "object") return;
    const id = category.id;
    if (!id) return;
    const entries = Array.isArray(category.entries) ? category.entries : [];
    byCategory.set(id, {
      id,
      name: category.name || toLabel(id),
      description: category.description || "",
      entries: entries
        .map((entry) => normalizeStatEntry(entry))
        .filter(Boolean)
        .sort((a, b) => a.id.localeCompare(b.id)),
    });
  });

  if (custom && typeof custom === "object") {
    Object.entries(custom).forEach(([categoryId, value]) => {
      if (!categoryId) return;
      const existing = byCategory.get(categoryId) || {
        id: categoryId,
        name: toLabel(categoryId),
        description: "",
        entries: [],
      };
      const customEntries = Array.isArray(value) ? value : [];
      customEntries
        .map((entry) => normalizeStatEntry(entry))
        .filter(Boolean)
        .forEach((entry) => {
          const hasEntry = existing.entries.some((existingEntry) => existingEntry.id === entry.id);
          if (!hasEntry) {
            existing.entries.push(entry);
          }
        });
      existing.entries.sort((a, b) => a.id.localeCompare(b.id));
      byCategory.set(categoryId, existing);
    });
  }

  return Array.from(byCategory.values()).sort((a, b) => a.id.localeCompare(b.id));
}

function registerSkill(skill) {
  state.skills[skill.id] = skill;
  state.currentSkillId = skill.id;
  state.registry.add(skill.id.toLowerCase());
}

function refreshValidation() {
  const currentSkill = state.currentSkillId ? state.skills[state.currentSkillId] : null;
  if (!currentSkill) {
    state.validation = {
      fields: {},
      valid: false,
    };
    return;
  }
  const result = validateSkill(currentSkill, {
    registry: state.registry,
    currentSkillId: state.currentSkillId,
  });
  state.validation = {
    fields: result.fields,
    valid: result.valid,
  };
}

function normalizeFieldValue(field, value) {
  if (field === "id" && typeof value === "string") {
    return value.trim().toLowerCase();
  }
  if (
    (field === "backgroundTexture" ||
      field === "iconTexture" ||
      field === "borderTexture") &&
    typeof value === "string"
  ) {
    return value.trim();
  }
  if (field === "titleColor" && typeof value === "string") {
    return value.trim().toUpperCase();
  }
  if (field === "isStartingPoint") {
    if (typeof value === "boolean") return value;
    if (typeof value === "string") {
      const lowered = value.trim().toLowerCase();
      return lowered === "true" || lowered === "1" || lowered === "yes" || lowered === "on";
    }
    return Boolean(value);
  }
  if (field === "positionX" || field === "positionY" || field === "buttonSize") {
    if (value === null || value === undefined) return value;
    if (typeof value === "number") return value;
    if (typeof value === "string") {
      const trimmed = value.trim();
      if (trimmed === "") return "";
      const numeric = Number(trimmed);
      return Number.isNaN(numeric) ? value : numeric;
    }
    return value;
  }
  if (field === "tags") {
    if (Array.isArray(value)) {
      return value
        .filter((entry) => typeof entry === "string")
        .map((entry) => entry.trim())
        .filter(Boolean);
    }
    if (typeof value === "string") {
      return value
        .split(",")
        .map((entry) => entry.trim())
        .filter(Boolean);
    }
    return [];
  }
  if (field === "directConnections" || field === "longConnections" || field === "oneWayConnections") {
    if (Array.isArray(value)) {
      return value
        .filter((entry) => typeof entry === "string")
        .map((entry) => entry.trim().toLowerCase())
        .filter(Boolean);
    }
    if (typeof value === "string") {
      return value
        .split(",")
        .map((entry) => entry.trim().toLowerCase())
        .filter(Boolean);
    }
    return [];
  }
  return value;
}

function setSkillField(skill, field, value) {
  const normalized = normalizeFieldValue(field, value);

  if (field === "id") {
    const currentId = state.currentSkillId || skill.id;
    const nextId = normalized;
    if (!nextId || typeof nextId !== "string") {
      skill.id = normalized;
      return skill;
    }
    const lowerCurrent = typeof currentId === "string" ? currentId.toLowerCase() : null;
    const lowerNext = nextId.toLowerCase();
    if (lowerCurrent === lowerNext) {
      skill.id = nextId;
      return skill;
    }
    if (state.registry.has(lowerNext) && lowerNext !== lowerCurrent) {
      // Preserve the typed value for UI feedback but do not re-key state
      skill.id = nextId;
      return skill;
    }
    if (currentId && state.skills[currentId]) {
      delete state.skills[currentId];
    }
    if (lowerCurrent) {
      state.registry.delete(lowerCurrent);
    }
    skill.id = nextId;
    state.skills[nextId] = skill;
    state.currentSkillId = nextId;
    state.registry.add(lowerNext);
    return skill;
  }

  skill[field] = normalized;
  return skill;
}

function updateSkillField(field, value) {
  const skill = state.currentSkillId ? state.skills[state.currentSkillId] : null;
  if (!skill) {
    return {
      success: false,
      error: "No skill selected.",
    };
  }

  setSkillField(skill, field, value);

  const validation = validateSkillField(field, skill[field], {
    registry: state.registry,
    currentSkillId: state.currentSkillId,
  });

  state.validation.fields[field] = validation;
  refreshValidation();
  notifyWatchers();

  return {
    success: validation.valid,
    validation,
  };
}

function updateSkillPartial(updates = {}) {
  const skill = state.currentSkillId ? state.skills[state.currentSkillId] : null;
  if (!skill) {
    return {
      success: false,
      error: "No skill selected.",
    };
  }

  Object.entries(updates).forEach(([field, value]) => {
    setSkillField(skill, field, value);
  });

  refreshValidation();
  notifyWatchers();

  return {
    success: state.validation.valid,
    validation: state.validation,
  };
}

function setCurrentSkill(skillId) {
  if (!skillId || !state.skills[skillId]) {
    console.warn("Attempted to switch to unknown skill", skillId);
    return false;
  }
  state.currentSkillId = skillId;
  refreshValidation();
  notifyWatchers();
  return true;
}

function getReferenceOptions(kind) {
  if (!kind) return [];
  switch (kind) {
    case "attributes":
      return state.references.attributes;
    case "enchantments":
      return state.references.enchantments;
    case "stats":
      return state.references.stats;
    case "items":
      return state.references.items;
    case "effects":
      return state.references.effects;
    default:
      return [];
  }
}

function setValueAtPath(target, path, value) {
  if (!Array.isArray(path) || path.length === 0) {
    return target;
  }
  const finalIndex = path.length - 1;
  let cursor = target;
  for (let index = 0; index < finalIndex; index += 1) {
    const key = path[index];
    const nextKey = path[index + 1];
    const requiresArray = typeof nextKey === "number";
    let nextValue = cursor[key];
    if (nextValue === undefined || nextValue === null || typeof nextValue !== "object") {
      nextValue = requiresArray ? [] : {};
      cursor[key] = nextValue;
    } else if (requiresArray && !Array.isArray(nextValue)) {
      nextValue = [];
      cursor[key] = nextValue;
    } else if (!requiresArray && Array.isArray(nextValue)) {
      nextValue = {};
      cursor[key] = nextValue;
    }
    cursor = nextValue;
  }
  const lastKey = path[finalIndex];
  if (value === undefined) {
    if (Array.isArray(cursor) && typeof lastKey === "number") {
      if (lastKey >= 0 && lastKey < cursor.length) {
        cursor.splice(lastKey, 1);
      }
    } else if (cursor && typeof cursor === "object") {
      delete cursor[lastKey];
    }
    return target;
  }
  const clonedValue = cloneValue(value);
  if (Array.isArray(cursor) && typeof lastKey === "number") {
    cursor[lastKey] = clonedValue;
  } else if (cursor && typeof cursor === "object") {
    cursor[lastKey] = clonedValue;
  }
  return target;
}

function addBonus(bonus) {
  const skill = getActiveSkill();
  if (!skill) {
    return {
      success: false,
      error: "No skill selected.",
    };
  }
  const bonuses = ensureBonusesArray(skill);
  const entry = cloneValue(bonus ?? {});
  bonuses.push(entry);
  refreshValidation();
  notifyWatchers();
  return {
    success: true,
    index: bonuses.length - 1,
    bonus: entry,
  };
}

function setBonus(index, bonus) {
  const skill = getActiveSkill();
  if (!skill) {
    return {
      success: false,
      error: "No skill selected.",
    };
  }
  const bonuses = ensureBonusesArray(skill);
  if (!Number.isInteger(index) || index < 0 || index >= bonuses.length) {
    return {
      success: false,
      error: "Bonus index is out of range.",
    };
  }
  const entry = cloneValue(bonus ?? {});
  bonuses.splice(index, 1, entry);
  refreshValidation();
  notifyWatchers();
  return {
    success: true,
    bonus: entry,
  };
}

function updateBonus(index, updates = {}) {
  const skill = getActiveSkill();
  if (!skill) {
    return {
      success: false,
      error: "No skill selected.",
    };
  }
  const bonuses = ensureBonusesArray(skill);
  if (!Number.isInteger(index) || index < 0 || index >= bonuses.length) {
    return {
      success: false,
      error: "Bonus index is out of range.",
    };
  }
  if (!updates || typeof updates !== "object") {
    return {
      success: false,
      error: "Bonus updates must be provided as an object.",
    };
  }
  const current = bonuses[index];
  if (!current || typeof current !== "object") {
    return {
      success: false,
      error: "Cannot update a non-object bonus entry.",
    };
  }
  const next = cloneValue(current);
  Object.entries(updates).forEach(([key, value]) => {
    next[key] = cloneValue(value);
  });
  bonuses.splice(index, 1, next);
  refreshValidation();
  notifyWatchers();
  return {
    success: true,
    bonus: next,
  };
}

function updateBonusByPath(index, path, value) {
  if (!Array.isArray(path) || path.length === 0) {
    if (!value || typeof value !== "object") {
      return {
        success: false,
        error: "Bonus updates require a path or object payload.",
      };
    }
    return updateBonus(index, value);
  }
  const skill = getActiveSkill();
  if (!skill) {
    return {
      success: false,
      error: "No skill selected.",
    };
  }
  const bonuses = ensureBonusesArray(skill);
  if (!Number.isInteger(index) || index < 0 || index >= bonuses.length) {
    return {
      success: false,
      error: "Bonus index is out of range.",
    };
  }
  const current = bonuses[index];
  if (!current || typeof current !== "object") {
    return {
      success: false,
      error: "Cannot update a non-object bonus entry.",
    };
  }
  const next = cloneValue(current);
  setValueAtPath(next, path, value);
  bonuses.splice(index, 1, next);
  refreshValidation();
  notifyWatchers();
  return {
    success: true,
    bonus: next,
  };
}

function removeBonus(index) {
  const skill = getActiveSkill();
  if (!skill) {
    return {
      success: false,
      error: "No skill selected.",
    };
  }
  const bonuses = ensureBonusesArray(skill);
  if (!Number.isInteger(index) || index < 0 || index >= bonuses.length) {
    return {
      success: false,
      error: "Bonus index is out of range.",
    };
  }
  const [removed] = bonuses.splice(index, 1);
  refreshValidation();
  notifyWatchers();
  return {
    success: true,
    bonus: removed ?? null,
  };
}

function moveBonus(fromIndex, toIndex) {
  const skill = getActiveSkill();
  if (!skill) {
    return {
      success: false,
      error: "No skill selected.",
    };
  }
  const bonuses = ensureBonusesArray(skill);
  if (bonuses.length === 0) {
    return {
      success: false,
      error: "No bonuses available to reorder.",
    };
  }
  if (!Number.isInteger(fromIndex) || fromIndex < 0 || fromIndex >= bonuses.length) {
    return {
      success: false,
      error: "Source index is out of range.",
    };
  }
  if (!Number.isInteger(toIndex)) {
    return {
      success: false,
      error: "Target index must be an integer.",
    };
  }
  const clampedTarget = Math.max(0, Math.min(toIndex, bonuses.length - 1));
  if (fromIndex === clampedTarget) {
    return {
      success: true,
      bonus: bonuses[fromIndex],
      index: fromIndex,
    };
  }
  const [entry] = bonuses.splice(fromIndex, 1);
  bonuses.splice(clampedTarget, 0, entry);
  refreshValidation();
  notifyWatchers();
  return {
    success: true,
    bonus: entry,
    index: clampedTarget,
  };
}

function addCustomAttribute(entry) {
  const normalized = normalizeAttributeEntry(entry, "custom");
  if (!normalized) {
    return {
      success: false,
      error: "Attribute must have an ID using namespace:path format.",
    };
  }
  const exists = state.customEntries.attributes.some((item) => item.id === normalized.id);
  if (!exists) {
    state.customEntries.attributes.push(normalized);
    state.references.attributes = mergeAttributeReferences(
      state.references.attributes,
      state.customEntries.attributes,
    );
    notifyWatchers();
  }
  return {
    success: true,
    entry: normalized,
  };
}

function addCustomEnchantment(entry) {
  const normalized = normalizeEnchantmentEntry(entry, "custom");
  if (!normalized) {
    return {
      success: false,
      error: "Enchantment must have an ID using namespace:path format.",
    };
  }
  const exists = state.customEntries.enchantments.some((item) => item.id === normalized.id);
  if (!exists) {
    state.customEntries.enchantments.push(normalized);
    state.references.enchantments = mergeEnchantmentReferences(
      state.references.enchantments,
      state.customEntries.enchantments,
    );
    notifyWatchers();
  }
  return {
    success: true,
    entry: normalized,
  };
}

function addCustomStat(categoryId, entry) {
  const normalizedCategory = typeof categoryId === "string" ? categoryId : "minecraft:custom";
  const normalizedEntry = normalizeStatEntry(entry);
  if (!normalizedEntry) {
    return {
      success: false,
      error: "Stat entries must use namespace:path format.",
    };
  }
  if (!state.customEntries.stats[normalizedCategory]) {
    state.customEntries.stats[normalizedCategory] = [];
  }
  const exists = state.customEntries.stats[normalizedCategory].some(
    (item) => item.id === normalizedEntry.id,
  );
  if (!exists) {
    state.customEntries.stats[normalizedCategory].push(normalizedEntry);
    state.references.stats = mergeStatReferences(state.references.stats, state.customEntries.stats);
    notifyWatchers();
  }
  return {
    success: true,
    entry: normalizedEntry,
  };
}

async function initializeState() {
  if (state.ready || state.loading) {
    return getStateSnapshot();
  }

  state.loading = true;
  state.error = null;
  notifyWatchers();

  try {
    const [
      metadata,
      attributes,
      enchantments,
      stats,
      custom,
      itemsData,
      effectsData,
    ] = await Promise.all([
      loadJSON(DATA_SOURCES.metadata),
      loadJSON(DATA_SOURCES.attributes),
      loadJSON(DATA_SOURCES.enchantments),
      loadJSON(DATA_SOURCES.stats),
      loadJSON(DATA_SOURCES.custom),
      loadJSON(DATA_SOURCES.items),
      loadJSON(DATA_SOURCES.effects),
    ]);

    state.metadata = metadata;

    state.customEntries = {
      attributes: Array.isArray(custom.attributes) ? custom.attributes : [],
      enchantments: Array.isArray(custom.enchantments) ? custom.enchantments : [],
      stats: custom.stats && typeof custom.stats === "object" ? custom.stats : {},
    };

    const vanillaAttributes = Array.isArray(attributes.attributes)
      ? attributes.attributes
      : [];
    const vanillaEnchantments = Array.isArray(enchantments.enchantments)
      ? enchantments.enchantments
      : [];
    const vanillaStats = Array.isArray(stats.categories) ? stats.categories : [];
    const vanillaItems = Array.isArray(itemsData?.items) ? itemsData.items : [];
    const vanillaEffects = Array.isArray(effectsData?.effects) ? effectsData.effects : [];

    state.references.attributes = mergeAttributeReferences(
      vanillaAttributes,
      state.customEntries.attributes,
    );
    state.references.enchantments = mergeEnchantmentReferences(
      vanillaEnchantments,
      state.customEntries.enchantments,
    );
    state.references.stats = mergeStatReferences(vanillaStats, state.customEntries.stats);
    state.references.items = buildItemReferences(vanillaItems);
    state.references.effects = buildEffectReferences(vanillaEffects);

    const defaultSkill = createDefaultSkill();
    registerSkill(defaultSkill);

    refreshValidation();

    state.ready = true;
    state.loading = false;
    state.error = null;
    notifyWatchers();
  } catch (error) {
    console.error("Failed to initialize state", error);
    state.error = error instanceof Error ? error.message : String(error);
    state.loading = false;
    notifyWatchers();
  }

  return getStateSnapshot();
}

function getCurrentSkill() {
  return state.currentSkillId ? state.skills[state.currentSkillId] : null;
}

function getSkillRegistry() {
  return Array.from(state.registry.values());
}

export {
  initializeState,
  subscribe,
  getStateSnapshot,
  getCurrentSkill,
  getReferenceOptions,
  getSkillRegistry,
  setCurrentSkill,
  updateSkillField,
  updateSkillPartial,
  addBonus,
  setBonus,
  updateBonus,
  updateBonusByPath,
  removeBonus,
  moveBonus,
  addCustomAttribute,
  addCustomEnchantment,
  addCustomStat,
  state as __state,
};
