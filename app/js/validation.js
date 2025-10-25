const NAMESPACE_PATH_REGEX = /^[a-z0-9_.-]+:[a-z0-9_./-]+$/i;
const HEX_COLOR_REGEX = /^[0-9a-f]{6}$/i;

function isNamespacePath(value) {
  if (typeof value !== "string") return false;
  return NAMESPACE_PATH_REGEX.test(value.trim());
}

function isHexColor(value) {
  if (typeof value !== "string" || value.trim() === "") return false;
  return HEX_COLOR_REGEX.test(value.trim());
}

function ensureArray(value) {
  if (Array.isArray(value)) return value;
  if (value === undefined || value === null || value === "") return [];
  return [value];
}

function findDuplicates(values) {
  const seen = new Set();
  const duplicates = new Set();
  values.forEach((item) => {
    const normalized = typeof item === "string" ? item.trim() : item;
    if (normalized === "" || normalized === null || normalized === undefined) {
      return;
    }
    if (seen.has(normalized)) {
      duplicates.add(normalized);
    } else {
      seen.add(normalized);
    }
  });
  return Array.from(duplicates.values());
}

function validateSkillField(field, value, context = {}) {
  const { registry = new Set(), currentSkillId = null } = context;
  const result = {
    field,
    valid: true,
    message: "",
  };

  switch (field) {
    case "id": {
      if (!value || typeof value !== "string" || value.trim() === "") {
        result.valid = false;
        result.message = "Skill ID is required.";
        break;
      }
      const trimmed = value.trim();
      if (!isNamespacePath(trimmed)) {
        result.valid = false;
        result.message = "ID must follow namespace:path format.";
        break;
      }
      const lowercaseId = trimmed.toLowerCase();
      const alreadyExists = registry.has(lowercaseId) && lowercaseId !== currentSkillId;
      if (alreadyExists) {
        result.valid = false;
        result.message = "Another skill already uses this ID.";
      }
      break;
    }
    case "title": {
      if (!value || typeof value !== "string" || value.trim() === "") {
        result.valid = false;
        result.message = "Title is required.";
      }
      break;
    }
    case "backgroundTexture":
    case "iconTexture":
    case "borderTexture": {
      if (!value || typeof value !== "string" || value.trim() === "") {
        result.valid = false;
        result.message = "A texture path is required.";
        break;
      }
      if (!value.includes(":")) {
        result.valid = false;
        result.message = "Texture must include a namespace (e.g. skilltree:textures/...).";
      }
      break;
    }
    case "titleColor": {
      if (value && typeof value === "string" && value.trim() !== "") {
        if (!isHexColor(value.trim())) {
          result.valid = false;
          result.message = "Use a 6 digit hex color code.";
        }
      }
      break;
    }
    case "positionX":
    case "positionY": {
      if (value === null || value === undefined || value === "") {
        result.valid = false;
        result.message = "Coordinate is required.";
        break;
      }
      const numberValue = Number(value);
      if (Number.isNaN(numberValue)) {
        result.valid = false;
        result.message = "Coordinate must be a number.";
      }
      break;
    }
    case "buttonSize": {
      if (value === null || value === undefined || value === "") {
        result.valid = false;
        result.message = "Button size is required.";
        break;
      }
      const numeric = Number(value);
      if (Number.isNaN(numeric)) {
        result.valid = false;
        result.message = "Button size must be numeric.";
      } else if (numeric <= 0) {
        result.valid = false;
        result.message = "Button size must be greater than zero.";
      }
      break;
    }
    case "isStartingPoint": {
      if (typeof value !== "boolean") {
        result.valid = false;
        result.message = "Starting point must be true or false.";
      }
      break;
    }
    case "tags": {
      const values = ensureArray(value).filter((entry) => typeof entry === "string" && entry.trim() !== "");
      const duplicates = findDuplicates(values.map((entry) => entry.trim().toLowerCase()));
      if (duplicates.length > 0) {
        result.valid = false;
        result.message = `Duplicate tags found: ${duplicates.join(", ")}.`;
      }
      break;
    }
    case "directConnections":
    case "longConnections":
    case "oneWayConnections": {
      const values = ensureArray(value).filter((entry) => typeof entry === "string" && entry.trim() !== "");
      if (values.some((entry) => !isNamespacePath(entry))) {
        result.valid = false;
        result.message = "Connections must use namespace:path IDs.";
        break;
      }
      const duplicates = findDuplicates(values.map((entry) => entry.trim().toLowerCase()))
        .map((duplicate) => duplicate.trim());
      if (duplicates.length > 0) {
        result.valid = false;
        result.message = `Duplicate connections found: ${duplicates.join(", ")}.`;
      }
      break;
    }
    default:
      break;
  }

  return result;
}

function validateSkill(skill, context = {}) {
  const fieldsToCheck = [
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
    "tags",
    "directConnections",
    "longConnections",
    "oneWayConnections",
  ];

  const fieldResults = {};
  let isValid = true;

  fieldsToCheck.forEach((field) => {
    fieldResults[field] = validateSkillField(field, skill[field], {
      ...context,
      currentSkillId: context.currentSkillId ?? skill.id,
    });
    if (!fieldResults[field].valid) {
      isValid = false;
    }
  });

  return {
    valid: isValid,
    fields: fieldResults,
  };
}

export {
  NAMESPACE_PATH_REGEX,
  HEX_COLOR_REGEX,
  isNamespacePath,
  isHexColor,
  findDuplicates,
  validateSkillField,
  validateSkill,
};
