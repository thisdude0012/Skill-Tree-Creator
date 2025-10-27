import {
  subscribe,
  getStateSnapshot,
  addBonus,
  setBonus,
  updateBonusByPath,
  removeBonus,
  moveBonus,
} from "./state.js";

const SELECTORS = {
  root: "[data-bonus-builder]",
  list: "[data-bonus-list]",
  typeSelect: "[data-bonus-type]",
  addButton: "[data-bonus-add]",
  emptyState: "[data-bonus-empty]",
};

const REFERENCE_TYPE_MAP = {
  "minecraft:attribute": "attributes",
  "minecraft:enchantment": "enchantments",
};

const ATTRIBUTE_MODIFIER_FIELDS = [
  { key: "id", label: "Modifier UUID", type: "string" },
  { key: "name", label: "Modifier Name", type: "string" },
  { key: "amount", label: "Modifier Amount", type: "number" },
  { key: "operation", label: "Operation", type: "enum:AttributeModifier.Operation" },
];

const EXPERIENCE_SOURCE_OPTIONS = [
  { value: "mobs", label: "Mobs" },
  { value: "fishing", label: "Fishing" },
  { value: "ore", label: "Ore" },
];

const EQUIPMENT_TYPE_OPTIONS = [
  { value: "any", label: "Any" },
  { value: "armor", label: "Armor" },
  { value: "axe", label: "Axe" },
  { value: "boots", label: "Boots" },
  { value: "bow", label: "Bow" },
  { value: "chestplate", label: "Chestplate" },
  { value: "crossbow", label: "Crossbow" },
  { value: "helmet", label: "Helmet" },
  { value: "hoe", label: "Hoe" },
  { value: "leggings", label: "Leggings" },
  { value: "melee_weapon", label: "Melee Weapon" },
  { value: "pickaxe", label: "Pickaxe" },
  { value: "ranged_weapon", label: "Ranged Weapon" },
  { value: "shield", label: "Shield" },
  { value: "shovel", label: "Shovel" },
  { value: "sword", label: "Sword" },
  { value: "tool", label: "Tool" },
  { value: "trident", label: "Trident" },
  { value: "weapon", label: "Weapon" },
];

const EFFECT_TYPE_OPTIONS = [
  { value: "any", label: "Any" },
  { value: "beneficial", label: "Beneficial" },
  { value: "harmful", label: "Harmful" },
  { value: "neutral", label: "Neutral" },
];

const TARGET_OPTIONS = [
  { value: "player", label: "Player" },
  { value: "enemy", label: "Enemy" },
];

const LOOT_TYPE_OPTIONS = [
  { value: "archaeology", label: "Archaeology" },
  { value: "chests", label: "Chests" },
  { value: "fishing", label: "Fishing" },
  { value: "mobs", label: "Mobs" },
  { value: "gems", label: "Gems" },
  { value: "ores", label: "Ores" },
];

const CURIOS_SLOT_PLACEHOLDERS = [
  { value: "belt", label: "Belt" },
  { value: "ring", label: "Ring" },
  { value: "necklace", label: "Necklace" },
  { value: "hands", label: "Hands" },
  { value: "charm", label: "Charm" },
];

const EXTRA_ATTRIBUTE_IDS = ["skilltree:exp_per_minute", "skilltree:regeneration"];
const COMBOBOX_MAX_RESULTS = 200;

const builderState = {
  root: null,
  list: null,
  typeSelect: null,
  addButton: null,
  emptyState: null,
  unsubscribe: null,
  metadata: null,
  bonusOptions: [],
  snapshot: null,
};

function initializeBonusBuilder() {
  const root = document.querySelector(SELECTORS.root);
  if (!root) {
    return null;
  }

  builderState.root = root;
  builderState.list = root.querySelector(SELECTORS.list);
  if (!builderState.list) {
    builderState.list = document.createElement("div");
    builderState.list.dataset.bonusList = "";
    builderState.list.className = "bonus-list";
    root.appendChild(builderState.list);
  }

  builderState.typeSelect = root.querySelector(SELECTORS.typeSelect);
  builderState.addButton = root.querySelector(SELECTORS.addButton);
  builderState.emptyState = root.querySelector(SELECTORS.emptyState);

  if (builderState.typeSelect) {
    builderState.typeSelect.addEventListener("change", handleAddTypeChange);
  }

  if (builderState.addButton) {
    builderState.addButton.addEventListener("click", handleAddBonus);
  }

  builderState.unsubscribe = subscribe(render);
  render(getStateSnapshot());

  return {
    destroy() {
      if (builderState.typeSelect) {
        builderState.typeSelect.removeEventListener("change", handleAddTypeChange);
      }
      if (builderState.addButton) {
        builderState.addButton.removeEventListener("click", handleAddBonus);
      }
      if (builderState.unsubscribe) {
        builderState.unsubscribe();
        builderState.unsubscribe = null;
      }
    },
  };
}

function render(snapshot) {
  builderState.snapshot = snapshot;
  builderState.metadata = snapshot.metadata ?? null;
  builderState.bonusOptions = buildBonusTypeOptions(builderState.metadata);

  updateToolbar();
  renderBonusList();
}

function updateToolbar() {
  const { typeSelect, addButton, bonusOptions, metadata, snapshot } = builderState;
  if (!typeSelect) {
    if (addButton) {
      addButton.disabled = true;
    }
    return;
  }

  const previousValue = typeSelect.value;
  populateTypeSelect(typeSelect, bonusOptions, previousValue);

  const ready = Boolean(snapshot?.ready && metadata);
  typeSelect.disabled = !ready;
  if (addButton) {
    addButton.disabled = !ready || !typeSelect.value;
  }
}

function handleAddTypeChange(event) {
  if (!builderState.addButton) return;
  const select = event.currentTarget;
  builderState.addButton.disabled = select.disabled || !select.value;
}

function handleAddBonus(event) {
  event.preventDefault();
  if (!builderState.metadata) {
    return;
  }
  const select = builderState.typeSelect;
  if (!select || !select.value) {
    if (select) {
      select.focus();
    }
    return;
  }
  
  const allCards = builderState.list?.querySelectorAll(".bonus-card");
  if (allCards) {
    allCards.forEach((card) => {
      card.dataset.collapsed = "true";
    });
  }
  
  const template = createBonusTemplate(select.value, builderState.metadata);
  addBonus(template);
}

function renderBonusList() {
  const { list, snapshot, metadata } = builderState;
  if (!list) {
    return;
  }

  const activeElement = document.activeElement;
  const activePath = activeElement && activeElement.dataset ? activeElement.dataset.bonusPath : null;
  const selection =
    activeElement && typeof activeElement.selectionStart === "number"
      ? { start: activeElement.selectionStart, end: activeElement.selectionEnd }
      : null;

  list.innerHTML = "";

  if (!snapshot || snapshot.loading) {
    updateEmptyState("Loading bonuses and metadata…");
    restoreFocus(activePath, selection);
    return;
  }

  if (!metadata) {
    updateEmptyState("Metadata not available. Bonuses cannot be edited.");
    restoreFocus(activePath, selection);
    return;
  }

  const skill = snapshot.currentSkill;
  if (!skill) {
    updateEmptyState("Select a skill to configure bonuses.");
    restoreFocus(activePath, selection);
    return;
  }

  const bonuses = Array.isArray(skill.bonuses) ? skill.bonuses : [];

  if (bonuses.length === 0) {
    updateEmptyState("No bonuses configured. Use the controls above to add one.");
    restoreFocus(activePath, selection);
    return;
  }

  updateEmptyState("");

  bonuses.forEach((bonus, index) => {
    const card = renderBonusCard(bonus, index);
    if (index === bonuses.length - 1) {
      card.dataset.collapsed = "false";
    } else {
      card.dataset.collapsed = "true";
    }
    list.appendChild(card);
  });

  restoreFocus(activePath, selection);
}

function renderBonusCard(bonus, index) {
  const card = document.createElement("article");
  card.className = "bonus-card";
  card.dataset.index = String(index);
  card.dataset.collapsed = "false";

  const header = document.createElement("header");
  header.className = "bonus-card__header";

  const title = document.createElement("h3");
  title.className = "bonus-card__title";
  const bonusId = stripNamespace(bonus?.type);
  const definition = getBonusDefinition(bonusId, builderState.metadata);
  title.textContent = definition?.label || formatLabel(bonusId || `Bonus ${index + 1}`);
  header.appendChild(title);

  const summary = document.createElement("p");
  summary.className = "bonus-card__summary";
  summary.textContent = generateBonusSummary(bonus, definition);
  header.appendChild(summary);

  const typeControls = document.createElement("div");
  typeControls.className = "bonus-card__type";

  const typeLabel = document.createElement("label");
  typeLabel.className = "bonus-card__type-label";
  typeLabel.textContent = "Type";

  const typeSelect = document.createElement("select");
  typeSelect.className = "form-input bonus-card__type-select";
  populateTypeSelect(typeSelect, builderState.bonusOptions, bonusId, "Change bonus type");
  typeSelect.dataset.bonusPath = encodeBonusPath(index, ["type"]);
  typeSelect.addEventListener("change", (event) => {
    const nextType = event.currentTarget.value;
    if (!nextType || nextType === bonusId) return;
    const template = createBonusTemplate(nextType, builderState.metadata);
    setBonus(index, template);
  });

  typeLabel.appendChild(typeSelect);
  typeControls.appendChild(typeLabel);
  header.appendChild(typeControls);

  const actions = document.createElement("div");
  actions.className = "bonus-card__actions";

  const collapseToggle = createActionButton("▼", "Collapse/Expand", () => {
    const isCollapsed = card.dataset.collapsed === "true";
    card.dataset.collapsed = isCollapsed ? "false" : "true";
  });
  collapseToggle.className = "bonus-card__action bonus-card__collapse-toggle";
  actions.appendChild(collapseToggle);

  const moveUp = createActionButton("↑", "Move up", () => moveBonus(index, index - 1));
  moveUp.disabled = index === 0;
  actions.appendChild(moveUp);

  const moveDown = createActionButton("↓", "Move down", () => moveBonus(index, index + 1));
  moveDown.disabled = index === (builderState.snapshot?.currentSkill?.bonuses?.length ?? 0) - 1;
  actions.appendChild(moveDown);

  const remove = createActionButton("✕", "Remove", () => removeBonus(index));
  actions.appendChild(remove);

  header.appendChild(actions);
  card.appendChild(header);

  const body = document.createElement("div");
  body.className = "bonus-card__body";

  if (!definition) {
    const message = document.createElement("p");
    message.className = "bonus-card__empty";
    message.textContent = "Unknown bonus type. The metadata does not describe its fields.";
    body.appendChild(message);
  } else {
    const context = {
      bonusId,
      bonusDefinition: definition,
      registryStack: [],
    };
    renderFieldGroup(body, definition.fields, bonus, index, [], [], context);
  }

  card.appendChild(body);
  return card;
}

function generateBonusSummary(bonus, definition) {
  if (!bonus || !definition) {
    return "No details available";
  }
  
  const parts = [];
  
  if (bonus.attribute) {
    parts.push(`Attribute: ${bonus.attribute}`);
  }
  if (bonus.enchantment) {
    parts.push(`Enchantment: ${bonus.enchantment}`);
  }
  if (bonus.effect) {
    parts.push(`Effect: ${bonus.effect}`);
  }
  if (bonus.amount !== undefined) {
    parts.push(`Amount: ${bonus.amount}`);
  }
  if (bonus.value !== undefined) {
    parts.push(`Value: ${bonus.value}`);
  }
  if (bonus.multiplier !== undefined) {
    parts.push(`Multiplier: ${bonus.multiplier}`);
  }
  
  if (parts.length === 0) {
    return "Click to expand and configure";
  }
  
  return parts.join(", ");
}

function renderFieldGroup(
  container,
  fields,
  data,
  bonusIndex,
  pathPrefix,
  ancestorTypes = [],
  context = {},
) {
  fields.forEach((field) => {
    const propertyNames = getPropertyNames(field);
    const valueType = field.type || "string";

    if (valueType.startsWith("registry:")) {
      const propertyName = propertyNames[0];
      const fieldValue = data ? data[propertyName] : undefined;
      const registryField = renderRegistryField(
        field,
        fieldValue,
        bonusIndex,
        [...pathPrefix, propertyName],
        ancestorTypes,
        context,
      );
      container.appendChild(registryField);
      return;
    }

    if (valueType === "attribute_modifier") {
      const group = renderAttributeModifierGroup(field, data, bonusIndex, pathPrefix, context);
      container.appendChild(group);
      return;
    }

    propertyNames.forEach((propertyName) => {
      const fieldValue = data ? data[propertyName] : undefined;
      const fieldElement = renderSimpleField(
        field,
        propertyName,
        fieldValue,
        bonusIndex,
        [...pathPrefix, propertyName],
        context,
      );
      if (fieldElement) {
        container.appendChild(fieldElement);
      }
    });
  });
}

function renderSimpleField(fieldMeta, propertyName, value, bonusIndex, path, context = {}) {
  const wrapper = document.createElement("div");
  wrapper.className = "bonus-field";

  const label = document.createElement("label");
  label.className = "bonus-field__label";
  label.textContent = formatLabel(propertyName);
  applyFieldHints(label, propertyName, context);
  wrapper.appendChild(label);

  const customControl = renderCustomField({
    fieldMeta,
    propertyName,
    value,
    bonusIndex,
    path,
    context,
    wrapper,
  });

  if (customControl === false) {
    return null;
  }
  if (customControl instanceof HTMLElement) {
    applyTargetDependency(wrapper, propertyName, customControl);
    return wrapper;
  }

  const valueType = fieldMeta.type || "string";

  if (valueType.startsWith("enum:")) {
    const select = renderEnumSelect(fieldMeta, value, bonusIndex, path);
    applyTargetDependency(wrapper, propertyName, select);
    wrapper.appendChild(select);
    return wrapper;
  }

  if (valueType === "boolean") {
    const input = document.createElement("input");
    input.type = "checkbox";
    input.className = "form-checkbox";
    input.checked = Boolean(value);
    input.dataset.bonusPath = encodeBonusPath(bonusIndex, path);
    input.addEventListener("change", (event) => {
      updateBonusByPath(bonusIndex, path, event.currentTarget.checked);
    });
    applyTargetDependency(wrapper, propertyName, input);
    wrapper.appendChild(input);
    return wrapper;
  }

  const input = document.createElement("input");
  input.className = "form-input";
  input.dataset.bonusPath = encodeBonusPath(bonusIndex, path);

  if (valueType === "number") {
    input.type = "number";
    input.step = "0.001";
    input.value = value ?? "";
    input.addEventListener("change", (event) => {
      const raw = event.currentTarget.value;
      if (raw === "") {
        updateBonusByPath(bonusIndex, path, "");
        return;
      }
      const numeric = Number(raw);
      updateBonusByPath(bonusIndex, path, Number.isNaN(numeric) ? raw : numeric);
    });
  } else {
    input.type = "text";
    input.value = value ?? "";
    input.addEventListener("input", (event) => {
      updateBonusByPath(bonusIndex, path, event.currentTarget.value);
    });
  }

  attachReferenceDatalist(input, valueType, propertyName);
  applyTargetDependency(wrapper, propertyName, input);
  wrapper.appendChild(input);
  return wrapper;
}

function renderEnumSelect(fieldMeta, value, bonusIndex, path) {
  const select = document.createElement("select");
  select.className = "form-input";
  select.dataset.bonusPath = encodeBonusPath(bonusIndex, path);

  const enumDef = getEnumDefinition(fieldMeta.type, builderState.metadata);
  const options = buildEnumOptions(enumDef);

  if (options.length === 0) {
    select.disabled = true;
    return select;
  }

  options.forEach((option) => {
    const optionElement = document.createElement("option");
    optionElement.value = option.value;
    optionElement.textContent = option.label;
    optionElement.dataset.enumType = option.dataType;
    select.appendChild(optionElement);
  });

  const match = options.find(
    (option) => option.raw === value || option.value === String(value),
  );
  select.value = match ? match.value : options[0].value;

  select.addEventListener("change", (event) => {
    const element = event.currentTarget;
    const selectedOption = element.selectedOptions[0];
    let nextValue = element.value;
    if (selectedOption?.dataset.enumType === "number") {
      nextValue = Number(nextValue);
    }
    updateBonusByPath(bonusIndex, path, nextValue);
  });

  return select;
}

function renderCustomField({
  fieldMeta,
  propertyName,
  value,
  bonusIndex,
  path,
  context,
  wrapper,
}) {
  const datasetPath = encodeBonusPath(bonusIndex, path);
  const valueType = fieldMeta.type || "string";
  const bonusId = context?.bonusId;

  if (valueType === "minecraft:attribute") {
    const options = getAttributeOptions();
    const control = createSearchableSelect({
      value: typeof value === "string" ? value : "",
      options,
      datasetPath,
      placeholder: "Search attributes…",
      onChange: (next) => {
        const normalized = typeof next === "string" ? next.trim() : "";
        updateBonusByPath(bonusIndex, path, normalized);
      },
    });
    wrapper.appendChild(control);
    return control;
  }

  if (bonusId === "grant_item" && propertyName === "item_id") {
    const options = getItemOptions();
    const control = createSearchableSelect({
      value: typeof value === "string" ? value : "",
      options,
      datasetPath,
      placeholder: "Search items…",
      onChange: (next) => {
        const normalized = typeof next === "string" ? next.trim() : "";
        updateBonusByPath(bonusIndex, path, normalized);
      },
    });
    wrapper.appendChild(control);
    return control;
  }

  if (fieldMeta.type === "minecraft:mob_effect_instance" && propertyName === "effect") {
    const options = getEffectOptions();
    const control = createSearchableSelect({
      value: typeof value === "string" ? value : "",
      options,
      datasetPath,
      placeholder: "Search effects…",
      onChange: (next) => {
        const normalized = typeof next === "string" ? next.trim() : "";
        updateBonusByPath(bonusIndex, path, normalized);
      },
    });
    wrapper.appendChild(control);
    return control;
  }

  if (fieldMeta.type === "minecraft:mob_effect_instance" && propertyName === "duration") {
    const control = createDurationInput({
      value,
      datasetPath,
      onCommit: (seconds) => {
        updateBonusByPath(bonusIndex, path, seconds);
      },
    });
    wrapper.appendChild(control);
    return control;
  }

  if (bonusId === "gained_experience" && propertyName === "experience_source") {
    const normalized = typeof value === "string" ? value.toLowerCase() : "";
    if (value && normalized !== value) {
      updateBonusByPath(bonusIndex, path, normalized);
    }
    const select = createSelectInput({
      value: normalized,
      options: EXPERIENCE_SOURCE_OPTIONS,
      datasetPath,
      onChange: (next) => {
        updateBonusByPath(bonusIndex, path, next);
      },
    });
    wrapper.appendChild(select);
    return select;
  }

  if (bonusId === "effect_duration" && propertyName === "effect_type") {
    const normalized = typeof value === "string" ? value.toLowerCase() : "";
    if (value && normalized !== value) {
      updateBonusByPath(bonusIndex, path, normalized);
    }
    const select = createSelectInput({
      value: normalized,
      options: EFFECT_TYPE_OPTIONS,
      datasetPath,
      onChange: (next) => {
        updateBonusByPath(bonusIndex, path, next);
      },
    });
    wrapper.appendChild(select);
    return select;
  }

  if (bonusId === "effect_duration" && propertyName === "target") {
    const normalized = typeof value === "string" ? value.toLowerCase() : "";
    if (value && normalized !== value) {
      updateBonusByPath(bonusIndex, path, normalized);
    }
    const select = createSelectInput({
      value: normalized,
      options: TARGET_OPTIONS,
      datasetPath,
      onChange: (next) => {
        updateBonusByPath(bonusIndex, path, next);
      },
    });
    wrapper.appendChild(select);
    return select;
  }

  if (propertyName === "target" && isWithinRegistry(context, "event_listeners")) {
    const normalized = typeof value === "string" ? value.toLowerCase() : "";
    if (value && normalized !== value) {
      updateBonusByPath(bonusIndex, path, normalized);
    }
    const select = createSelectInput({
      value: normalized,
      options: TARGET_OPTIONS,
      datasetPath,
      onChange: (next) => {
        updateBonusByPath(bonusIndex, path, next);
      },
    });
    wrapper.appendChild(select);
    return select;
  }

  if (propertyName === "equipment_type" && isItemConditionEquipmentContext(context)) {
    const normalized = typeof value === "string" ? value.toLowerCase() : "any";
    if (value && normalized !== value) {
      updateBonusByPath(bonusIndex, path, normalized);
    }
    const select = createSelectInput({
      value: normalized,
      options: EQUIPMENT_TYPE_OPTIONS,
      datasetPath,
      onChange: (next) => {
        updateBonusByPath(bonusIndex, path, next);
      },
    });
    wrapper.appendChild(select);
    return select;
  }

  if (bonusId === "curio_slots" && propertyName === "slot") {
    const control = createSearchableSelect({
      value: typeof value === "string" ? value : "",
      options: CURIOS_SLOT_PLACEHOLDERS,
      datasetPath,
      placeholder: "Select or type slot…",
      onChange: (next) => {
        const normalized = typeof next === "string" ? next.trim() : "";
        updateBonusByPath(bonusIndex, path, normalized);
      },
    });
    wrapper.appendChild(control);
    return control;
  }

  if (bonusId === "inflict_damage" && propertyName === "damage_type") {
    const normalized = typeof value === "string" ? value.toLowerCase() : "";
    if (value && normalized !== value) {
      updateBonusByPath(bonusIndex, path, normalized);
    }
    const select = createSelectInput({
      value: normalized,
      options: getDamageConditionOptions(),
      datasetPath,
      placeholder: "Select damage type",
      onChange: (next) => {
        updateBonusByPath(bonusIndex, path, next || "");
      },
    });
    wrapper.appendChild(select);
    return select;
  }

  if (bonusId === "loot_duplication" && propertyName === "loot_type") {
    const normalized = typeof value === "string" ? value.toLowerCase() : "";
    if (value && normalized !== value) {
      updateBonusByPath(bonusIndex, path, normalized);
    }
    const select = createSelectInput({
      value: normalized,
      options: LOOT_TYPE_OPTIONS,
      datasetPath,
      onChange: (next) => {
        updateBonusByPath(bonusIndex, path, next);
      },
    });
    wrapper.appendChild(select);
    return select;
  }

  return null;
}

function createSelectInput({ value = "", options = [], datasetPath, placeholder, onChange }) {
  const select = document.createElement("select");
  select.className = "form-input";
  if (datasetPath) {
    select.dataset.bonusPath = datasetPath;
  }

  if (placeholder) {
    const placeholderOption = document.createElement("option");
    placeholderOption.value = "";
    placeholderOption.textContent = placeholder;
    select.appendChild(placeholderOption);
  }

  options.forEach((option) => {
    const optionElement = document.createElement("option");
    optionElement.value = option.value;
    optionElement.textContent = option.label || formatLabel(option.value);
    select.appendChild(optionElement);
  });

  if (value && options.some((option) => option.value === value)) {
    select.value = value;
  } else if (!placeholder && options.length > 0) {
    select.value = options[0].value;
  } else {
    select.value = "";
  }

  if (typeof onChange === "function") {
    select.addEventListener("change", (event) => {
      onChange(event.currentTarget.value);
    });
  }

  if (options.length === 0) {
    select.disabled = true;
  }

  return select;
}

function createSearchableSelect({
  value = "",
  options = [],
  datasetPath,
  placeholder = "",
  allowCustom = true,
  onChange,
}) {
  const container = document.createElement("div");
  container.className = "searchable-select";

  const input = document.createElement("input");
  input.type = "search";
  input.className = "form-input searchable-select__input";
  input.autocomplete = "off";
  input.spellcheck = false;
  input.placeholder = placeholder;
  input.value = value ?? "";
  if (datasetPath) {
    input.dataset.bonusPath = datasetPath;
  }
  container.appendChild(input);

  const toggle = document.createElement("button");
  toggle.type = "button";
  toggle.className = "searchable-select__toggle";
  toggle.setAttribute("aria-label", "Toggle options");
  toggle.setAttribute("aria-haspopup", "listbox");
  toggle.setAttribute("aria-expanded", "false");
  toggle.innerHTML = "▾";
  container.appendChild(toggle);

  const panel = document.createElement("div");
  panel.className = "searchable-select__panel";
  panel.hidden = true;

  const list = document.createElement("ul");
  list.className = "searchable-select__list";
  list.setAttribute("role", "listbox");
  panel.appendChild(list);
  container.appendChild(panel);

  const normalizedOptions = Array.isArray(options)
    ? options.map((option) => {
        const resolvedLabel =
          typeof option.label === "string" && option.label.trim()
            ? option.label.trim()
            : formatLabel(option.value);
        const metaSource =
          typeof option.meta === "string" && option.meta.trim()
            ? option.meta.trim()
            : typeof option.category === "string" && option.category.trim()
              ? formatLabel(option.category.trim())
              : "";
        return {
          value: option.value,
          label: resolvedLabel,
          meta: metaSource,
        };
      })
    : [];

  let isOpen = false;
  let currentValue = typeof value === "string" ? value : "";

  const commit = (nextValue) => {
    if (typeof nextValue !== "string") {
      nextValue = "";
    }
    if (nextValue === currentValue) {
      return;
    }
    currentValue = nextValue;
    if (typeof onChange === "function") {
      onChange(nextValue);
    }
  };

  const handleOutside = (event) => {
    if (!container.contains(event.target)) {
      closePanel();
    }
  };

  const updateOptions = () => {
    const term = input.value.trim().toLowerCase();
    let filtered = normalizedOptions;
    if (term) {
      filtered = normalizedOptions.filter((option) => {
        const labelMatch = option.label.toLowerCase().includes(term);
        const valueMatch = option.value.toLowerCase().includes(term);
        const metaMatch = option.meta ? String(option.meta).toLowerCase().includes(term) : false;
        return labelMatch || valueMatch || metaMatch;
      });
    }
    if (filtered.length > COMBOBOX_MAX_RESULTS) {
      filtered = filtered.slice(0, COMBOBOX_MAX_RESULTS);
    }

    list.innerHTML = "";
    if (filtered.length === 0) {
      const empty = document.createElement("li");
      empty.className = "searchable-select__empty";
      empty.textContent = "No matches";
      list.appendChild(empty);
      return;
    }

    filtered.forEach((option) => {
      const item = document.createElement("li");
      item.className = "searchable-select__option";

      const button = document.createElement("button");
      button.type = "button";
      button.className = "searchable-select__option-button";
      button.setAttribute("role", "option");
      button.dataset.value = option.value;
      button.textContent = option.label;
      if (option.meta) {
        const meta = document.createElement("span");
        meta.className = "searchable-select__option-meta";
        meta.setAttribute("aria-hidden", "true");
        meta.textContent = option.meta;
        button.appendChild(meta);
      }
      const isSelected = option.value === currentValue;
      if (isSelected) {
        button.classList.add("is-selected");
      }
      button.setAttribute("aria-selected", isSelected ? "true" : "false");
      button.addEventListener("click", () => {
        input.value = option.value;
        commit(option.value);
        closePanel();
      });

      item.appendChild(button);
      list.appendChild(item);
    });
  };

  const openPanel = () => {
    if (isOpen || normalizedOptions.length === 0) {
      return;
    }
    isOpen = true;
    panel.hidden = false;
    list.scrollTop = 0;
    container.classList.add("is-open");
    toggle.setAttribute("aria-expanded", "true");
    updateOptions();
    document.addEventListener("pointerdown", handleOutside);
  };

  const closePanel = () => {
    if (!isOpen) {
      return;
    }
    isOpen = false;
    panel.hidden = true;
    container.classList.remove("is-open");
    toggle.setAttribute("aria-expanded", "false");
    document.removeEventListener("pointerdown", handleOutside);
  };

  input.addEventListener("focus", () => {
    if (normalizedOptions.length > 0) {
      openPanel();
    }
  });

  input.addEventListener("input", () => {
    if (allowCustom) {
      commit(input.value);
    }
    if (isOpen) {
      updateOptions();
    }
  });

  input.addEventListener("change", () => {
    if (allowCustom) {
      commit(input.value);
    }
  });

  input.addEventListener("keydown", (event) => {
    if (event.key === "Escape" && isOpen) {
      event.preventDefault();
      closePanel();
    } else if (event.key === "ArrowDown" && !isOpen) {
      event.preventDefault();
      openPanel();
    }
  });

  toggle.addEventListener("click", (event) => {
    event.preventDefault();
    if (isOpen) {
      closePanel();
    } else {
      openPanel();
      input.focus();
    }
  });

  return container;
}

function createDurationInput({ value, datasetPath, onCommit }) {
  const container = document.createElement("div");
  container.className = "timer-input";

  const input = document.createElement("input");
  input.type = "text";
  input.className = "form-input timer-input__display";
  input.placeholder = "MM:SS";
  if (datasetPath) {
    input.dataset.bonusPath = datasetPath;
  }

  const initialNumeric =
    typeof value === "number" ? value : typeof value === "string" ? Number(value) : 0;
  let seconds = Number.isFinite(initialNumeric) && initialNumeric > 0 ? Math.floor(initialNumeric) : 0;

  const render = () => {
    input.value = formatSecondsToTimer(seconds);
  };

  const commitSeconds = (nextValue, force = false) => {
    const numeric = Number(nextValue);
    const fallback = Number.isFinite(numeric) ? numeric : seconds;
    const clamped = Math.max(0, Math.floor(fallback));
    const changed = clamped !== seconds;
    seconds = clamped;
    render();
    if ((changed || force) && typeof onCommit === "function") {
      onCommit(seconds);
    }
  };

  const commitFromInput = () => {
    const parsed = parseTimerInput(input.value);
    if (parsed === null) {
      render();
      return;
    }
    commitSeconds(parsed, true);
  };

  const focusInput = () => {
    requestAnimationFrame(() => {
      input.focus();
    });
  };

  input.addEventListener("blur", commitFromInput);
  input.addEventListener("change", commitFromInput);
  input.addEventListener("focus", () => {
    input.select();
  });
  input.addEventListener("keydown", (event) => {
    if (event.key === "ArrowUp") {
      event.preventDefault();
      commitSeconds(seconds + 1);
    } else if (event.key === "ArrowDown") {
      event.preventDefault();
      commitSeconds(seconds - 1);
    } else if (event.key === "Enter") {
      event.preventDefault();
      commitFromInput();
    } else if (event.key === "Escape") {
      event.preventDefault();
      render();
    }
  });

  const controls = document.createElement("div");
  controls.className = "timer-input__controls";

  const increase = document.createElement("button");
  increase.type = "button";
  increase.className = "timer-input__button timer-input__button--up";
  increase.setAttribute("aria-label", "Increase duration");
  increase.textContent = "▲";
  increase.addEventListener("click", () => {
    commitSeconds(seconds + 1);
    focusInput();
  });

  const decrease = document.createElement("button");
  decrease.type = "button";
  decrease.className = "timer-input__button timer-input__button--down";
  decrease.setAttribute("aria-label", "Decrease duration");
  decrease.textContent = "▼";
  decrease.addEventListener("click", () => {
    commitSeconds(seconds - 1);
    focusInput();
  });

  controls.appendChild(increase);
  controls.appendChild(decrease);
  container.appendChild(input);
  container.appendChild(controls);

  render();

  return container;
}

function applyFieldHints(label, propertyName, context) {
  if (!label) {
    return;
  }
  if (context?.bonusId === "inflict_damage") {
    // Both the damage condition (attack trigger) and damage type (damage source)
    // are present in existing Inflict Damage skills (see skills/warrior/warrior_9.json),
    // so we keep both controls and surface guidance via tooltips.
    if (propertyName === "damage_condition") {
      label.title = "Choose the combat condition that triggers the damage (e.g., melee or ranged attacks).";
    }
    if (propertyName === "damage_type") {
      label.title = "Select the damage source that will be applied when the bonus triggers.";
    }
  }
}

function applyTargetDependency(wrapper, propertyName, control) {
  if (!wrapper) {
    return;
  }
  if (typeof propertyName === "string" && propertyName.startsWith("enemy_")) {
    wrapper.dataset.targetVisibility = "enemy";
    wrapper.classList.add("bonus-field--conditional");
  }
  if (propertyName === "target" && control) {
    attachTargetVisibility(control, wrapper);
  }
}

function attachTargetVisibility(control, wrapper) {
  if (!control) {
    return;
  }
  const scope = wrapper?.parentElement;
  if (!scope) {
    return;
  }
  const update = () => {
    const rawValue = typeof control.value === "string" ? control.value.toLowerCase() : "";
    const showEnemy = rawValue === "enemy";
    scope.querySelectorAll("[data-target-visibility=\"enemy\"]").forEach((element) => {
      element.classList.toggle("bonus-field--hidden", !showEnemy);
    });
  };
  control.addEventListener("change", update);
  control.addEventListener("input", update);
  update();
}

function parseTimerInput(raw) {
  if (typeof raw !== "string") {
    return null;
  }
  const trimmed = raw.trim();
  if (trimmed === "") {
    return null;
  }
  if (/^\d+$/.test(trimmed)) {
    const seconds = Number(trimmed);
    return Number.isNaN(seconds) ? null : seconds;
  }
  const match = trimmed.match(/^(\d{1,4}):(\d{1,2})$/);
  if (!match) {
    return null;
  }
  const minutes = Number(match[1]);
  const secondsPart = Number(match[2]);
  if (Number.isNaN(minutes) || Number.isNaN(secondsPart) || secondsPart >= 60) {
    return null;
  }
  return minutes * 60 + secondsPart;
}

function formatSecondsToTimer(totalSeconds) {
  if (!Number.isFinite(totalSeconds)) {
    return "00:00";
  }
  const safeValue = Math.max(0, Math.floor(totalSeconds));
  const minutes = Math.floor(safeValue / 60);
  const seconds = safeValue % 60;
  return `${String(minutes).padStart(2, "0")}:${String(seconds).padStart(2, "0")}`;
}

function getCurrentRegistryContext(context) {
  const stack = context?.registryStack;
  if (!Array.isArray(stack) || stack.length === 0) {
    return null;
  }
  return stack[stack.length - 1];
}

function matchesRegistryId(actualId, expected) {
  if (!actualId || !expected) {
    return false;
  }
  if (actualId === expected) {
    return true;
  }
  return actualId.endsWith(expected);
}

function isWithinRegistry(context, registryId) {
  const current = getCurrentRegistryContext(context);
  if (!current) {
    return false;
  }
  return matchesRegistryId(current.registryId, registryId);
}

function isItemConditionEquipmentContext(context) {
  const current = getCurrentRegistryContext(context);
  if (!current) {
    return false;
  }
  return matchesRegistryId(current.registryId, "item_conditions") && current.entryId === "equipment_type";
}

function getDamageConditionOptions() {
  const registry = builderState.metadata?.registries?.damageConditions;
  if (!registry?.entries) {
    return [];
  }
  const options = Object.values(registry.entries).map((entry) => ({
    value: entry.id,
    label: entry.label || formatLabel(entry.id),
  }));
  options.sort((a, b) => a.label.localeCompare(b.label));
  const noneIndex = options.findIndex((option) => option.value === "none");
  if (noneIndex > 0) {
    const [noneOption] = options.splice(noneIndex, 1);
    options.unshift(noneOption);
  }
  return options;
}

function getAttributeOptions() {
  const references = builderState.snapshot?.references?.attributes;
  const entries = new Map();
  if (Array.isArray(references)) {
    references.forEach((entry) => {
      if (entry?.id) {
        const fallbackLabel = formatLabel(stripNamespace(entry.id));
        const rawLabel =
          (typeof entry.name === "string" && entry.name.trim()) || fallbackLabel;
        const cleanedLabel =
          sanitizeAttributeLabel(rawLabel) || sanitizeAttributeLabel(fallbackLabel);
        const category = typeof entry.category === "string" ? entry.category.trim() : "";
        const meta =
          category && category.toLowerCase() !== "generic"
            ? formatLabel(category)
            : "";
        entries.set(entry.id, {
          value: entry.id,
          label: cleanedLabel,
          meta,
        });
      }
    });
  }
  EXTRA_ATTRIBUTE_IDS.forEach((id) => {
    if (!entries.has(id)) {
      entries.set(id, {
        value: id,
        label: sanitizeAttributeLabel(formatLabel(stripNamespace(id))),
        meta: "Modded",
      });
    }
  });
  return Array.from(entries.values()).sort((a, b) => a.label.localeCompare(b.label));
}

function sanitizeAttributeLabel(label) {
  if (!label || typeof label !== "string") {
    return "";
  }
  return label.replace(/\s*Generic$/i, "").replace(/\s+/g, " ").trim();
}

function getEffectOptions() {
  const references = builderState.snapshot?.references?.effects;
  if (!Array.isArray(references)) {
    return [];
  }
  return references
    .filter((entry) => entry?.id)
    .map((entry) => ({
      value: entry.id,
      label: entry.name || formatLabel(stripNamespace(entry.id)),
      meta: entry.category ? formatLabel(entry.category) : "",
    }))
    .sort((a, b) => a.label.localeCompare(b.label));
}

function getItemOptions() {
  const references = builderState.snapshot?.references?.items;
  if (!Array.isArray(references)) {
    return [];
  }
  return references
    .filter((entry) => entry?.id)
    .map((entry) => ({
      value: entry.id,
      label: entry.name || formatLabel(stripNamespace(entry.id)),
      meta: entry.category ? formatLabel(entry.category) : "",
    }))
    .sort((a, b) => a.label.localeCompare(b.label));
}

function renderRegistryField(
  fieldMeta,
  value,
  bonusIndex,
  path,
  ancestorTypes = [],
  context = {},
) {
  const wrapper = document.createElement("section");
  wrapper.className = "bonus-nested";

  const header = document.createElement("header");
  header.className = "bonus-nested__header";

  const title = document.createElement("h4");
  title.className = "bonus-nested__title";
  title.textContent = formatLabel(fieldMeta.name);
  header.appendChild(title);

  const registrySelect = document.createElement("select");
  registrySelect.className = "form-input bonus-nested__select";

  const registry = getRegistryDefinition(fieldMeta.type, builderState.metadata);
  const entries = getSortedRegistryEntries(registry);
  const selectedId = stripNamespace(value?.type);
  const ancestorSet = new Set(Array.isArray(ancestorTypes) ? ancestorTypes : []);

  if (entries.length === 0) {
    registrySelect.disabled = true;
  }

  entries.forEach((entry) => {
    const option = document.createElement("option");
    option.value = entry.id;
    option.textContent = entry.label || formatLabel(entry.id);
    if (ancestorSet.has(entry.id) && entry.id !== selectedId) {
      option.disabled = true;
      option.title = "Cannot select this option recursively.";
    }
    registrySelect.appendChild(option);
  });

  if (selectedId && entries.some((entry) => entry.id === selectedId)) {
    registrySelect.value = selectedId;
  }

  registrySelect.dataset.bonusPath = encodeBonusPath(bonusIndex, path.concat("type"));

  registrySelect.addEventListener("change", (event) => {
    const nextId = event.currentTarget.value;
    const nextEntry = entries.find((entry) => entry.id === nextId);
    const nextValue = buildRegistryValue(nextEntry, builderState.metadata);
    updateBonusByPath(bonusIndex, path, nextValue);
  });

  header.appendChild(registrySelect);
  wrapper.appendChild(header);

  const body = document.createElement("div");
  body.className = "bonus-nested__body";

  if (entries.length === 0) {
    const emptyMessage = document.createElement("p");
    emptyMessage.className = "bonus-card__empty";
    emptyMessage.textContent = "No metadata entries available for this field.";
    body.appendChild(emptyMessage);
    wrapper.appendChild(body);
    return wrapper;
  }

  let currentEntry = entries.find((entry) => entry.id === registrySelect.value);
  if (!currentEntry) {
    currentEntry = entries.find((entry) => entry.id === selectedId) || entries[0];
    if (currentEntry) {
      registrySelect.value = currentEntry.id;
    }
  }

  if (currentEntry) {
    const nextAncestors = ancestorSet.has(currentEntry.id)
      ? Array.from(ancestorSet)
      : [...ancestorSet, currentEntry.id];
    const nextContext = {
      ...context,
      registryStack: [
        ...(context?.registryStack ?? []),
        {
          registryId: registry?.id || null,
          entryId: currentEntry.id,
          path,
        },
      ],
    };
    renderFieldGroup(
      body,
      currentEntry.fields,
      value || buildRegistryValue(currentEntry, builderState.metadata),
      bonusIndex,
      path,
      nextAncestors,
      nextContext,
    );
  }

  wrapper.appendChild(body);
  return wrapper;
}

function renderAttributeModifierGroup(fieldMeta, data, bonusIndex, pathPrefix, context) {
  const container = document.createElement("fieldset");
  container.className = "bonus-group";

  const legend = document.createElement("legend");
  legend.className = "bonus-group__title";
  legend.textContent = "Attribute Modifier";
  container.appendChild(legend);

  ATTRIBUTE_MODIFIER_FIELDS.forEach((subField) => {
    const subPath = [...pathPrefix, subField.key];
    const value = data ? data[subField.key] : undefined;
    if (subField.key === "operation") {
      const fieldDefinition = { ...fieldMeta, type: subField.type };
      const fieldElement = renderSimpleField(
        fieldDefinition,
        subField.key,
        value,
        bonusIndex,
        subPath,
        context,
      );
      container.appendChild(fieldElement);
      return;
    }

    const fauxMeta = { ...fieldMeta, type: subField.type };
    const fieldElement = renderSimpleField(
      fauxMeta,
      subField.key,
      value,
      bonusIndex,
      subPath,
      context,
    );
    const label = fieldElement.querySelector(".bonus-field__label");
    if (label) {
      label.textContent = subField.label;
    }
    container.appendChild(fieldElement);
  });

  const uuidPath = encodeBonusPath(bonusIndex, [...pathPrefix, "id"]);
  const uuidField = container.querySelector(`[data-bonus-path="${uuidPath}"]`);
  if (uuidField && uuidField.tagName === "INPUT") {
    const helperRow = document.createElement("div");
    helperRow.className = "bonus-group__actions";
    const button = document.createElement("button");
    button.type = "button";
    button.className = "bonus-group__action";
    button.textContent = "Generate UUID";
    button.addEventListener("click", () => {
      const nextValue = generateUUID();
      uuidField.value = nextValue;
      updateBonusByPath(bonusIndex, [...pathPrefix, "id"], nextValue);
    });
    helperRow.appendChild(button);
    container.appendChild(helperRow);
  }

  return container;
}

function attachReferenceDatalist(input, valueType, propertyName) {
  const referenceKind = REFERENCE_TYPE_MAP[valueType];
  if (!referenceKind || !builderState.snapshot?.references) {
    return;
  }
  const options = builderState.snapshot.references[referenceKind];
  if (!Array.isArray(options) || options.length === 0) {
    return;
  }
  const datalist = document.createElement("datalist");
  const suffix = (input.dataset.bonusPath || `${propertyName}`)
    .replace(/[^a-zA-Z0-9_-]/g, "-");
  const datalistId = `bonus-ref-${suffix}`;
  datalist.id = datalistId;
  options.forEach((option) => {
    const element = document.createElement("option");
    element.value = option.id;
    element.label = option.name || formatLabel(option.id);
    datalist.appendChild(element);
  });
  input.setAttribute("list", datalistId);
  input.parentElement?.appendChild(datalist);
}

function populateTypeSelect(select, options, selectedValue, placeholderLabel = "Select bonus type") {
  const current = select.value;
  select.innerHTML = "";

  const placeholder = document.createElement("option");
  placeholder.value = "";
  placeholder.textContent = placeholderLabel;
  select.appendChild(placeholder);

  options.forEach((option) => {
    const element = document.createElement("option");
    element.value = option.id;
    element.textContent = option.label;
    select.appendChild(element);
  });

  const hasSelected = selectedValue && options.some((option) => option.id === selectedValue);
  const nextValue = hasSelected ? selectedValue : current;
  if (nextValue && options.some((option) => option.id === nextValue)) {
    select.value = nextValue;
  } else {
    select.value = "";
  }
}

function buildBonusTypeOptions(metadata) {
  const registry = metadata?.registries?.skillBonuses;
  if (!registry || !registry.entries) {
    return [];
  }
  return Object.values(registry.entries)
    .map((entry) => ({
      id: entry.id,
      label: entry.label || formatLabel(entry.id),
    }))
    .sort((a, b) => a.label.localeCompare(b.label));
}

function createBonusTemplate(typeId, metadata) {
  const normalizedType = withNamespace(typeId);
  const definition = getBonusDefinition(stripNamespace(normalizedType), metadata);
  const template = {
    type: normalizedType,
  };
  if (!definition) {
    return template;
  }
  definition.fields.forEach((field) => {
    applyFieldDefault(template, field, metadata);
  });
  return template;
}

function applyFieldDefault(target, fieldMeta, metadata) {
  const propertyNames = getPropertyNames(fieldMeta);
  const valueType = fieldMeta.type || "string";

  if (valueType.startsWith("registry:")) {
    const propertyName = propertyNames[0];
    const registry = getRegistryDefinition(valueType, metadata);
    const entry = pickDefaultRegistryEntry(registry, fieldMeta.default);
    if (entry) {
      target[propertyName] = buildRegistryValue(entry, metadata);
    }
    return;
  }

  if (valueType === "attribute_modifier") {
    ATTRIBUTE_MODIFIER_FIELDS.forEach((field) => {
      if (target[field.key] !== undefined) {
        return;
      }
      if (field.key === "id") {
        target.id = generateUUID();
      } else if (field.key === "name") {
        target.name = "Skill";
      } else if (field.key === "amount") {
        target.amount = 1;
      } else if (field.key === "operation") {
        target.operation = 0;
      }
    });
    return;
  }

  propertyNames.forEach((property) => {
    if (target[property] !== undefined) {
      return;
    }
    let resolved;
    if (Object.prototype.hasOwnProperty.call(fieldMeta, "default")) {
      resolved = coerceDefaultValue(fieldMeta, fieldMeta.default, metadata);
    } else if (valueType.startsWith("enum:")) {
      const enumDef = getEnumDefinition(valueType, metadata);
      resolved = getEnumDefaultValue(enumDef);
    } else {
      resolved = getTypeFallbackValue(valueType, fieldMeta.optional);
    }
    if (resolved !== undefined) {
      target[property] = resolved;
    }
  });
}

function coerceDefaultValue(fieldMeta, rawValue, metadata) {
  const valueType = fieldMeta.type || "string";
  if (valueType === "number") {
    const numeric = Number(rawValue);
    return Number.isNaN(numeric) ? undefined : numeric;
  }
  if (valueType === "boolean") {
    if (typeof rawValue === "boolean") return rawValue;
    if (typeof rawValue === "string") {
      const lowered = rawValue.toLowerCase();
      return lowered === "true" || lowered === "1" || lowered === "yes";
    }
    return Boolean(rawValue);
  }
  if (valueType.startsWith("enum:")) {
    const enumDef = getEnumDefinition(valueType, metadata);
    if (!enumDef) {
      return rawValue;
    }
    const entry = enumDef.values.find(
      (item) => item.id === rawValue || item.value === rawValue,
    );
    if (entry) {
      if (entry.value !== undefined) {
        return entry.value;
      }
      const index = enumDef.values.indexOf(entry);
      return index;
    }
    const numeric = Number(rawValue);
    return Number.isNaN(numeric) ? rawValue : numeric;
  }
  if (typeof rawValue === "string") {
    return rawValue;
  }
  return rawValue;
}

function getEnumDefaultValue(enumDef) {
  if (!enumDef || !Array.isArray(enumDef.values) || enumDef.values.length === 0) {
    return undefined;
  }
  const first = enumDef.values[0];
  if (first.value !== undefined) {
    return first.value;
  }
  return 0;
}

function getTypeFallbackValue(type, optional) {
  if (optional) {
    return undefined;
  }
  switch (type) {
    case "number":
      return 0;
    case "boolean":
      return false;
    default:
      return "";
  }
}

function getBonusDefinition(typeId, metadata) {
  if (!typeId || !metadata?.registries?.skillBonuses) {
    return null;
  }
  return metadata.registries.skillBonuses.entries?.[typeId] || null;
}

function getPropertyNames(fieldMeta) {
  if (Array.isArray(fieldMeta.jsonProperties) && fieldMeta.jsonProperties.length > 0) {
    return fieldMeta.jsonProperties;
  }
  return [fieldMeta.name];
}

function getRegistryDefinition(type, metadata) {
  if (!type.startsWith("registry:")) {
    return null;
  }
  const registryId = type.split(":")[1];
  const registries = metadata?.registries;
  if (!registries) {
    return null;
  }
  const directMatch = Object.values(registries).find((entry) => entry.id === registryId);
  if (directMatch) {
    return directMatch;
  }
  const camelKey = registryId.replace(/_([a-z])/g, (_, letter) => letter.toUpperCase());
  if (camelKey && registries[camelKey]) {
    return registries[camelKey];
  }
  return (
    Object.values(registries).find((entry) => entry.id.endsWith(registryId)) ?? null
  );
}

function getEnumDefinition(type, metadata) {
  if (!type.startsWith("enum:")) {
    return null;
  }
  const enumType = type.split(":")[1];
  const enums = metadata?.enums;
  if (!enums) {
    return null;
  }
  return (
    Object.values(enums).find((entry) => entry.type === enumType) ?? null
  );
}

function getSortedRegistryEntries(registry) {
  if (!registry || !registry.entries) {
    return [];
  }
  return Object.values(registry.entries).sort((a, b) => {
    const labelA = a.label || formatLabel(a.id);
    const labelB = b.label || formatLabel(b.id);
    return labelA.localeCompare(labelB);
  });
}

function pickDefaultRegistryEntry(registry, preferredId) {
  const entries = getSortedRegistryEntries(registry);
  if (entries.length === 0) {
    return null;
  }
  if (preferredId) {
    const normalized = stripNamespace(preferredId);
    const match = entries.find((entry) => entry.id === normalized);
    if (match) {
      return match;
    }
  }
  const noneEntry = entries.find((entry) => entry.id === "none");
  if (noneEntry) {
    return noneEntry;
  }
  return entries[0];
}

function buildRegistryValue(entry, metadata) {
  if (!entry) {
    return {
      type: "",
    };
  }
  const value = {
    type: withNamespace(entry.id),
  };
  entry.fields.forEach((field) => {
    applyFieldDefault(value, field, metadata);
  });
  return value;
}

function buildEnumOptions(enumDef) {
  if (!enumDef || !Array.isArray(enumDef.values)) {
    return [];
  }
  return enumDef.values.map((entry, index) => {
    const rawValue = entry.value !== undefined ? entry.value : index;
    const dataType = typeof rawValue === "number" ? "number" : typeof rawValue;
    return {
      value: String(rawValue),
      raw: rawValue,
      label: entry.label || formatLabel(entry.id || String(rawValue)),
      dataType,
    };
  });
}

function createActionButton(symbol, label, handler) {
  const button = document.createElement("button");
  button.type = "button";
  button.className = "bonus-card__action";
  button.title = label;
  button.textContent = symbol;
  button.addEventListener("click", (event) => {
    event.preventDefault();
    handler();
  });
  return button;
}

function updateEmptyState(message) {
  if (!builderState.emptyState) {
    return;
  }
  if (!message) {
    builderState.emptyState.textContent = "";
    builderState.emptyState.hidden = true;
    return;
  }
  builderState.emptyState.textContent = message;
  builderState.emptyState.hidden = false;
}

function restoreFocus(path, selection) {
  if (!path) {
    return;
  }
  const list = builderState.list;
  if (!list) {
    return;
  }
  const target = list.querySelector(`[data-bonus-path="${path}"]`);
  if (!target) {
    return;
  }
  target.focus();
  if (selection && typeof target.setSelectionRange === "function") {
    target.setSelectionRange(selection.start, selection.end);
  }
}

function formatLabel(id) {
  if (!id || typeof id !== "string") {
    return "";
  }
  return id
    .replace(/[_-]+/g, " ")
    .replace(/\s+/g, " ")
    .trim()
    .replace(/\b\w/g, (letter) => letter.toUpperCase());
}

function stripNamespace(value) {
  if (typeof value !== "string") {
    return value;
  }
  return value.includes(":") ? value.split(":")[1] : value;
}

function withNamespace(value) {
  if (typeof value !== "string" || value === "") {
    return value;
  }
  return value.includes(":") ? value : `skilltree:${value}`;
}

function encodePath(path) {
  return path
    .map((segment) => (typeof segment === "number" ? segment.toString() : segment))
    .join(".");
}

function encodeBonusPath(bonusIndex, path) {
  const segments = Array.isArray(path) ? path : [path];
  return encodePath([bonusIndex, ...segments]);
}

function generateUUID() {
  if (typeof crypto !== "undefined" && typeof crypto.randomUUID === "function") {
    return crypto.randomUUID();
  }
  let d = new Date().getTime();
  let d2 = (typeof performance !== "undefined" && performance.now && performance.now() * 1000) || 0;
  return "xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx".replace(/[xy]/g, (c) => {
    let r = Math.random() * 16;
    if (d > 0) {
      r = (d + r) % 16 | 0;
      d = Math.floor(d / 16);
    } else {
      r = (d2 + r) % 16 | 0;
      d2 = Math.floor(d2 / 16);
    }
    return (c === "x" ? r : (r & 0x3) | 0x8).toString(16);
  });
}

export { initializeBonusBuilder };
