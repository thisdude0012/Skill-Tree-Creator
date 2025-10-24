import {
  subscribe,
  getStateSnapshot,
  addBonus,
  setBonus,
  updateBonusByPath,
  removeBonus,
  moveBonus,
} from "../scripts/state.js";

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
    list.appendChild(card);
  });

  restoreFocus(activePath, selection);
}

function renderBonusCard(bonus, index) {
  const card = document.createElement("article");
  card.className = "bonus-card";
  card.dataset.index = String(index);

  const header = document.createElement("header");
  header.className = "bonus-card__header";

  const title = document.createElement("h3");
  title.className = "bonus-card__title";
  const bonusId = stripNamespace(bonus?.type);
  const definition = getBonusDefinition(bonusId, builderState.metadata);
  title.textContent = definition?.label || formatLabel(bonusId || `Bonus ${index + 1}`);
  header.appendChild(title);

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
    renderFieldGroup(body, definition.fields, bonus, index, []);
  }

  card.appendChild(body);
  return card;
}

function renderFieldGroup(container, fields, data, bonusIndex, pathPrefix) {
  fields.forEach((field) => {
    const propertyNames = getPropertyNames(field);
    const valueType = field.type || "string";

    if (valueType.startsWith("registry:")) {
      const propertyName = propertyNames[0];
      const fieldValue = data ? data[propertyName] : undefined;
      const registryField = renderRegistryField(field, fieldValue, bonusIndex, [...pathPrefix, propertyName]);
      container.appendChild(registryField);
      return;
    }

    if (valueType === "attribute_modifier") {
      const group = renderAttributeModifierGroup(field, data, bonusIndex, pathPrefix);
      container.appendChild(group);
      return;
    }

    propertyNames.forEach((propertyName) => {
      const fieldValue = data ? data[propertyName] : undefined;
      const fieldElement = renderSimpleField(field, propertyName, fieldValue, bonusIndex, [...pathPrefix, propertyName]);
      container.appendChild(fieldElement);
    });
  });
}

function renderSimpleField(fieldMeta, propertyName, value, bonusIndex, path) {
  const wrapper = document.createElement("div");
  wrapper.className = "bonus-field";

  const label = document.createElement("label");
  label.className = "bonus-field__label";
  label.textContent = formatLabel(propertyName);
  wrapper.appendChild(label);

  const valueType = fieldMeta.type || "string";

  if (valueType.startsWith("enum:")) {
    const select = renderEnumSelect(fieldMeta, value, bonusIndex, path);
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

function renderRegistryField(fieldMeta, value, bonusIndex, path) {
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

  if (entries.length === 0) {
    registrySelect.disabled = true;
  }

  entries.forEach((entry) => {
    const option = document.createElement("option");
    option.value = entry.id;
    option.textContent = entry.label || formatLabel(entry.id);
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

  const currentEntry = entries.find((entry) => entry.id === selectedId) || entries[0];
  if (currentEntry) {
    renderFieldGroup(
      body,
      currentEntry.fields,
      value || buildRegistryValue(currentEntry, builderState.metadata),
      bonusIndex,
      path,
    );
  }

  wrapper.appendChild(body);
  return wrapper;
}

function renderAttributeModifierGroup(fieldMeta, data, bonusIndex, pathPrefix) {
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
      const fieldElement = renderSimpleField(fieldDefinition, subField.key, value, bonusIndex, subPath);
      container.appendChild(fieldElement);
      return;
    }

    const fauxMeta = { ...fieldMeta, type: subField.type };
    const fieldElement = renderSimpleField(fauxMeta, subField.key, value, bonusIndex, subPath);
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
  return (
    Object.values(registries).find((entry) => entry.id === registryId) ?? null
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
