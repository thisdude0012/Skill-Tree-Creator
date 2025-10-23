#!/usr/bin/env node

import fs from "fs";
import path from "path";
import { fileURLToPath } from "url";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const PROJECT_ROOT = path.resolve(__dirname, "..");
const JAVA_ROOT = path.join(PROJECT_ROOT, "PST - Extracted data & assets", "PST - Converted Class Files");
const OUTPUT_PATH = path.join(PROJECT_ROOT, "app", "data", "metadata.json");

const registryConfigs = [
  {
    key: "skillBonuses",
    label: "Skill Bonuses",
    registryId: "skill_bonuses",
    file: path.join("daripher", "skilltree", "init", "PSTSkillBonuses.java"),
    entryKind: "bonus",
  },
  {
    key: "livingConditions",
    label: "Living Conditions",
    registryId: "living_conditions",
    file: path.join("daripher", "skilltree", "init", "PSTLivingConditions.java"),
    entryKind: "living_condition",
  },
  {
    key: "damageConditions",
    label: "Damage Conditions",
    registryId: "damage_conditions",
    file: path.join("daripher", "skilltree", "init", "PSTDamageConditions.java"),
    entryKind: "damage_condition",
  },
  {
    key: "itemConditions",
    label: "Item Conditions",
    registryId: "item_conditions",
    file: path.join("daripher", "skilltree", "init", "PSTItemConditions.java"),
    entryKind: "item_condition",
  },
  {
    key: "enchantmentConditions",
    label: "Enchantment Conditions",
    registryId: "enchantment_conditions",
    file: path.join("daripher", "skilltree", "init", "PSTEnchantmentConditions.java"),
    entryKind: "enchantment_condition",
  },
  {
    key: "livingMultipliers",
    label: "Living Multipliers",
    registryId: "skill_bonus_multipliers",
    file: path.join("daripher", "skilltree", "init", "PSTLivingMultipliers.java"),
    entryKind: "living_multiplier",
  },
  {
    key: "numericValueProviders",
    label: "Numeric Value Providers",
    registryId: "numeric_value_providers",
    file: path.join("daripher", "skilltree", "init", "PSTNumericValueProviders.java"),
    entryKind: "numeric_value_provider",
  },
  {
    key: "skillRequirements",
    label: "Skill Requirements",
    registryId: "skill_requirements",
    file: path.join("daripher", "skilltree", "init", "PSTSkillRequirements.java"),
    entryKind: "skill_requirement",
  },
];

const helperDescriptors = {
  Attribute: {
    type: "minecraft:attribute",
    propertyNames: ["attribute"],
    classField: "attribute",
  },
  AttributeModifier: {
    type: "attribute_modifier",
    propertyNames: ["id", "name", "amount", "operation"],
    classField: "modifier",
  },
  Operation: {
    type: "enum:AttributeModifier.Operation",
    propertyNames: ["operation"],
    classField: "operation",
  },
  LivingMultiplier: {
    type: "registry:living_multipliers",
    dynamicProperty: true,
    classField: "playerMultiplier",
  },
  LivingCondition: {
    type: "registry:living_conditions",
    dynamicProperty: true,
    classField: "playerCondition",
  },
  DamageCondition: {
    type: "registry:damage_conditions",
    propertyNames: ["damage_condition"],
    classField: "damageCondition",
  },
  ItemCondition: {
    type: "registry:item_conditions",
    propertyNames: ["item_condition"],
    classField: "itemCondition",
  },
  EventListener: {
    type: "registry:event_listeners",
    propertyNames: ["event_listener"],
    classField: "eventListener",
  },
  Effect: {
    type: "minecraft:mob_effect",
    propertyNames: ["effect"],
    classField: "effect",
  },
  PotionType: {
    type: "enum:PotionCondition.Type",
    propertyNames: ["potion_type"],
    classField: "type",
  },
  EffectInstance: {
    type: "minecraft:mob_effect_instance",
    propertyNames: ["effect", "duration", "amplifier"],
    classField: "effect",
  },
  ValueProvider: {
    type: "registry:numeric_value_providers",
    propertyNames: ["value_provider"],
    classField: "valueProvider",
  },
};

function buildClassIndex() {
  const bySimple = new Map();
  const byFull = new Map();

  function walk(dir) {
    const entries = fs.readdirSync(dir, { withFileTypes: true });
    for (const entry of entries) {
      if (entry.isDirectory()) {
        walk(path.join(dir, entry.name));
        continue;
      }
      if (!entry.name.endsWith(".java")) continue;
      const filePath = path.join(dir, entry.name);
      const content = fs.readFileSync(filePath, "utf8");
      const packageMatch = content.match(/package\s+([a-zA-Z0-9_.]+);/);
      const packageName = packageMatch ? packageMatch[1] : null;
      const simpleName = entry.name.replace(/\.java$/, "");
      const fullName = packageName ? `${packageName}.${simpleName}` : simpleName;
      const info = {
        simpleName,
        fullName,
        filePath,
        relativePath: path.relative(PROJECT_ROOT, filePath),
      };
      if (!bySimple.has(simpleName)) {
        bySimple.set(simpleName, []);
      }
      bySimple.get(simpleName).push(info);
      byFull.set(fullName, info);
    }
  }

  walk(JAVA_ROOT);
  return { bySimple, byFull };
}

const classIndex = buildClassIndex();

function toLabel(id) {
  return id
    .split(/[_\-]+/)
    .filter(Boolean)
    .map((part) => part.charAt(0).toUpperCase() + part.slice(1))
    .join(" ");
}

function resolveClassInfo(name, imports) {
  if (name.includes(".")) {
    const info = classIndex.byFull.get(name);
    if (!info) {
      throw new Error(`Unable to resolve class '${name}'`);
    }
    return info;
  }
  if (imports.has(name)) {
    const fullName = imports.get(name);
    const info = classIndex.byFull.get(fullName);
    if (!info) {
      throw new Error(`Import for '${name}' resolved to '${fullName}', but class was not indexed`);
    }
    return info;
  }
  const candidates = classIndex.bySimple.get(name);
  if (!candidates || candidates.length === 0) {
    throw new Error(`Unable to resolve class '${name}'`);
  }
  if (candidates.length > 1) {
    throw new Error(
      `Ambiguous class name '${name}': ${candidates.map((c) => c.fullName).join(", ")}`
    );
  }
  return candidates[0];
}

function parseImports(content) {
  const importRegex = /import\s+([a-zA-Z0-9_.]+);/g;
  const imports = new Map();
  let match;
  while ((match = importRegex.exec(content))) {
    const fullName = match[1];
    const simpleName = fullName.substring(fullName.lastIndexOf(".") + 1);
    imports.set(simpleName, fullName);
  }
  return imports;
}

function parseRegistry(config) {
  const filePath = path.join(JAVA_ROOT, config.file);
  const content = fs.readFileSync(filePath, "utf8");
  const imports = parseImports(content);
  const registerRegex = /REGISTRY\.register\("([^"]+)",\s*([A-Za-z0-9_\.]+)::new\)/g;
  const entries = [];
  let match;
  while ((match = registerRegex.exec(content))) {
    const id = match[1];
    const classExpr = match[2];
    const baseName = classExpr.replace(/\.Serializer$/, "");
    const resolved = resolveClassInfo(baseName, imports);
    entries.push({
      id,
      label: toLabel(id),
      classInfo: resolved,
    });
  }
  if (entries.length === 0) {
    throw new Error(`No registry entries parsed for ${config.registryId}`);
  }
  return {
    ...config,
    entries,
    sourceFile: path.relative(PROJECT_ROOT, filePath),
  };
}

function extractBlock(source, startIndex) {
  const startBrace = source.indexOf("{", startIndex);
  if (startBrace === -1) {
    return null;
  }
  let depth = 1;
  for (let i = startBrace + 1; i < source.length; i += 1) {
    const ch = source[i];
    if (ch === "\"") {
      i = skipString(source, i);
      continue;
    }
    if (ch === "'") {
      i = skipString(source, i, "'");
      continue;
    }
    if (ch === "{") depth += 1;
    else if (ch === "}") depth -= 1;
    if (depth === 0) {
      return {
        start: startBrace,
        end: i,
        body: source.slice(startBrace + 1, i),
      };
    }
  }
  return null;
}

function skipString(source, index, quote = '"') {
  let i = index + 1;
  while (i < source.length) {
    const ch = source[i];
    if (ch === "\\") {
      i += 2;
      continue;
    }
    if (ch === quote) {
      return i;
    }
    i += 1;
  }
  return source.length - 1;
}

function extractSerializerBody(classSource) {
  const marker = "class Serializer";
  const serializerIndex = classSource.indexOf(marker);
  if (serializerIndex === -1) {
    throw new Error("Serializer class not found");
  }
  const block = extractBlock(classSource, serializerIndex + marker.length);
  if (!block) {
    throw new Error("Unable to extract serializer body");
  }
  return block.body;
}

function extractJsonMethodBody(source, methodName) {
  const regex = new RegExp(`\\bpublic\\s+[^\\{]+\\s+${methodName}\\s*\\(\\s*JsonObject\\s+json`, "m");
  const match = regex.exec(source);
  if (!match) {
    return null;
  }
  const block = extractBlock(source, match.index + match[0].length);
  return block ? block.body : null;
}

function extractMethodBodyByName(source, methodName) {
  const regex = new RegExp(`\\bpublic\\s+[^\\{]+\\s+${methodName}\\s*\\(`, "m");
  const match = regex.exec(source);
  if (!match) {
    return null;
  }
  const block = extractBlock(source, match.index + match[0].length);
  return block ? block.body : null;
}

function splitArguments(argumentString) {
  const args = [];
  let current = "";
  let depth = 0;
  let inSingle = false;
  let inDouble = false;
  for (let i = 0; i < argumentString.length; i += 1) {
    const ch = argumentString[i];
    if (ch === "\\") {
      current += ch;
      i += 1;
      if (i < argumentString.length) current += argumentString[i];
      continue;
    }
    if (ch === "'" && !inDouble) {
      inSingle = !inSingle;
      current += ch;
      continue;
    }
    if (ch === '"' && !inSingle) {
      inDouble = !inDouble;
      current += ch;
      continue;
    }
    if (inSingle || inDouble) {
      current += ch;
      continue;
    }
    if (ch === "(" || ch === "[" || ch === "{") {
      depth += 1;
      current += ch;
      continue;
    }
    if (ch === ")" || ch === "]" || ch === "}") {
      depth -= 1;
      current += ch;
      continue;
    }
    if (ch === "," && depth === 0) {
      args.push(current.trim());
      current = "";
      continue;
    }
    current += ch;
  }
  if (current.trim()) {
    args.push(current.trim());
  }
  return args;
}

function splitStatements(methodBody) {
  const statements = [];
  let current = "";
  let depth = 0;
  let inSingle = false;
  let inDouble = false;
  for (let i = 0; i < methodBody.length; i += 1) {
    const ch = methodBody[i];
    if (ch === "\\") {
      current += ch;
      i += 1;
      if (i < methodBody.length) current += methodBody[i];
      continue;
    }
    if (ch === "'" && !inDouble) {
      inSingle = !inSingle;
      current += ch;
      continue;
    }
    if (ch === '"' && !inSingle) {
      inDouble = !inDouble;
      current += ch;
      continue;
    }
    if (inSingle || inDouble) {
      current += ch;
      continue;
    }
    if (ch === "{" || ch === "[" || ch === "(") {
      depth += 1;
      current += ch;
      continue;
    }
    if (ch === "}" || ch === "]" || ch === ")") {
      depth -= 1;
      current += ch;
      continue;
    }
    if (ch === ";" && depth === 0) {
      const statement = current.trim();
      if (statement) statements.push(statement);
      current = "";
      continue;
    }
    current += ch;
  }
  const tail = current.trim();
  if (tail) statements.push(tail);
  return statements;
}

function splitEnumConstants(enumBody) {
  const constants = [];
  let current = "";
  let depth = 0;
  let inSingle = false;
  let inDouble = false;
  for (let i = 0; i < enumBody.length; i += 1) {
    const ch = enumBody[i];
    if (ch === "\\") {
      current += ch;
      i += 1;
      if (i < enumBody.length) current += enumBody[i];
      continue;
    }
    if (ch === "'" && !inDouble) {
      inSingle = !inSingle;
      current += ch;
      continue;
    }
    if (ch === '"' && !inSingle) {
      inDouble = !inDouble;
      current += ch;
      continue;
    }
    if (inSingle || inDouble) {
      current += ch;
      continue;
    }
    if (ch === "(") {
      depth += 1;
      current += ch;
      continue;
    }
    if (ch === ")") {
      depth -= 1;
      current += ch;
      continue;
    }
    if (ch === "," && depth === 0) {
      constants.push(current.trim());
      current = "";
      continue;
    }
    current += ch;
  }
  const tail = current.trim();
  if (tail) constants.push(tail);
  return constants;
}

function extractBalancedSegment(source, openIndex, openChar = "(", closeChar = ")") {
  if (openIndex < 0 || source[openIndex] !== openChar) {
    return null;
  }
  let depth = 1;
  let inSingle = false;
  let inDouble = false;
  let i = openIndex + 1;
  let content = "";
  while (i < source.length && depth > 0) {
    const ch = source[i];
    if (ch === "\\") {
      content += ch;
      i += 1;
      if (i < source.length) {
        content += source[i];
      }
      i += 1;
      continue;
    }
    if (ch === "'" && !inDouble) {
      inSingle = !inSingle;
      content += ch;
      i += 1;
      continue;
    }
    if (ch === '"' && !inSingle) {
      inDouble = !inDouble;
      content += ch;
      i += 1;
      continue;
    }
    if (inSingle || inDouble) {
      content += ch;
      i += 1;
      continue;
    }
    if (ch === openChar) {
      depth += 1;
      content += ch;
      i += 1;
      continue;
    }
    if (ch === closeChar) {
      depth -= 1;
      if (depth === 0) {
        return { content, endIndex: i };
      }
      content += ch;
      i += 1;
      continue;
    }
    content += ch;
    i += 1;
  }
  return null;
}

function toSnakeCase(name) {
  if (!name) return name;
  return name
    .replace(/([a-z0-9])([A-Z])/g, "$1_$2")
    .replace(/([A-Z])([A-Z][a-z])/g, "$1_$2")
    .toLowerCase();
}

function trackField(metadata, fieldKey, updater) {
  if (!metadata.fields.has(fieldKey)) {
    metadata.fields.set(fieldKey, {
      name: fieldKey,
      optional: false,
      sources: new Set(),
      jsonProperties: new Set(),
    });
  }
  const field = metadata.fields.get(fieldKey);
  updater(field);
}

function parseSerializeMethod(methodBody) {
  const instanceVarMatch = methodBody.match(/([A-Za-z0-9_<>\.]+)\s+([A-Za-z0-9_]+)\s*=\s*\((?:[^)]+)\)\s*[A-Za-z0-9_]+/);
  const instanceVar = instanceVarMatch ? instanceVarMatch[2] : null;
  const metadata = {
    fields: new Map(),
  };
  const statements = splitStatements(methodBody);
  for (const statement of statements) {
    if (statement.startsWith("SerializationHelper.serialize")) {
      const helperMatch = statement.match(/SerializationHelper\.serialize([A-Za-z0-9_]+)\(\s*json\s*,\s*(.+)\)/);
      if (!helperMatch) continue;
      const helperName = helperMatch[1];
      const args = splitArguments(helperMatch[2]);
      const descriptor = helperDescriptors[helperName];
      if (!descriptor) {
        continue;
      }
      const fieldExpr = args[0];
      let classField = null;
      if (instanceVar) {
        const fieldMatch = fieldExpr.match(new RegExp(`${instanceVar}\\.([A-Za-z0-9_]+)`));
        if (fieldMatch) {
          classField = fieldMatch[1];
        }
      }
      let propertyNames;
      if (descriptor.dynamicProperty && args.length > 1) {
        propertyNames = [args[1].replace(/^["']|["']$/g, "")];
      } else {
        propertyNames = descriptor.propertyNames;
      }
      const fieldKey = propertyNames && propertyNames.length === 1 ? propertyNames[0] : descriptor.classField;
      trackField(metadata, fieldKey, (field) => {
        field.type = descriptor.type;
        if (classField) field.classField = classField;
        field.sources.add("serialize");
        if (propertyNames) propertyNames.forEach((name) => field.jsonProperties.add(name));
        field.helper = helperName;
      });
      continue;
    }
    if (statement.startsWith("json.addProperty")) {
      const propertyMatch = statement.match(/json\.addProperty\(\s*"([^"]+)"\s*,\s*(.+)\)/);
      if (!propertyMatch) continue;
      const propertyName = propertyMatch[1];
      const expression = propertyMatch[2];
      let classField = null;
      if (instanceVar) {
        const classFieldMatch = expression.match(new RegExp(`${instanceVar}\\.([A-Za-z0-9_]+)`));
        if (classFieldMatch) {
          classField = classFieldMatch[1];
        }
      }
      const fieldKey = propertyName;
      trackField(metadata, fieldKey, (field) => {
        if (classField) field.classField = classField;
        field.sources.add("serialize");
        field.jsonProperties.add(propertyName);
      });
      continue;
    }
    if (statement.startsWith("json.add(")) {
      const propertyMatch = statement.match(/json\.add\(\s*"([^"]+)"/);
      if (!propertyMatch) continue;
      const propertyName = propertyMatch[1];
      const fieldKey = propertyName;
      trackField(metadata, fieldKey, (field) => {
        field.sources.add("serialize");
        field.jsonProperties.add(propertyName);
      });
    }
  }
  return metadata;
}

function parseDeserializeMethod(methodBody, serializeData) {
  const metadata = serializeData;
  const statements = splitStatements(methodBody);
  const variableToField = new Map();
  for (const statement of statements) {
    if (statement.startsWith("return")) continue;
    const helperMatch = statement.match(/([A-Za-z0-9_<>\.]+)\s+([A-Za-z0-9_]+)\s*=\s*SerializationHelper\.deserialize([A-Za-z0-9_]+)\(\s*json(.*)\)/);
    if (helperMatch) {
      const helperName = helperMatch[3];
      const descriptor = helperDescriptors[helperName];
      if (!descriptor) continue;
      const args = splitArguments(helperMatch[4].replace(/^\s*,\s*/, ""));
      let fieldNames;
      if (descriptor.dynamicProperty && args.length > 0) {
        fieldNames = [args[0].replace(/^["']|["']$/g, "")];
      } else {
        fieldNames = descriptor.propertyNames;
      }
      const variableName = helperMatch[2];
      if (fieldNames && fieldNames.length === 1) {
        variableToField.set(variableName, fieldNames[0]);
        trackField(metadata, fieldNames[0], (field) => {
          field.type = descriptor.type;
          field.sources.add("deserialize");
          if (descriptor.propertyNames) descriptor.propertyNames.forEach((name) => field.jsonProperties.add(name));
        });
      }
      continue;
    }
    const getMatch = statement.match(/([A-Za-z0-9_<>\.]+)\s+([A-Za-z0-9_]+)\s*=\s*json\.get\("([^"]+)"\)\.getAs([A-Za-z0-9_]+)\(\)/);
    if (getMatch) {
      const propertyName = getMatch[3];
      const type = getMatch[4];
      const variableName = getMatch[2];
      variableToField.set(variableName, propertyName);
      trackField(metadata, propertyName, (field) => {
        field.type = inferTypeFromAccessor(type);
        field.sources.add("deserialize");
        field.jsonProperties.add(propertyName);
      });
      continue;
    }
    const elementMatch = statement.match(/([A-Za-z0-9_<>\.]+)\s+([A-Za-z0-9_]+)\s*=\s*SerializationHelper\.getElement\(json,\s*"([^"]+)"\)\.getAs([A-Za-z0-9_]+)\(\)/);
    if (elementMatch) {
      const propertyName = elementMatch[3];
      const type = elementMatch[4];
      const variableName = elementMatch[2];
      variableToField.set(variableName, propertyName);
      trackField(metadata, propertyName, (field) => {
        field.type = inferTypeFromAccessor(type);
        field.sources.add("deserialize");
        field.jsonProperties.add(propertyName);
      });
      continue;
    }
    const resourceLocationMatch = statement.match(/([A-Za-z0-9_<>\.]+)\s+([A-Za-z0-9_]+)\s*=\s*new\s+ResourceLocation\(json\.get\("([^"]+)"\)\.getAsString\(\)\)/);
    if (resourceLocationMatch) {
      const propertyName = resourceLocationMatch[3];
      const variableName = resourceLocationMatch[2];
      variableToField.set(variableName, propertyName);
      trackField(metadata, propertyName, (field) => {
        field.type = "minecraft:resource_location";
        field.sources.add("deserialize");
        field.jsonProperties.add(propertyName);
      });
      continue;
    }
    const hasMatch = statement.match(/json\.has\("([^"]+)"\)/);
    if (hasMatch) {
      const propertyName = hasMatch[1];
      trackField(metadata, propertyName, (field) => {
        field.optional = true;
      });
      continue;
    }
    const assignMatch = statement.match(/([A-Za-z0-9_]+)\.([A-Za-z0-9_]+)\s*=\s*([A-Za-z0-9_]+)/);
    if (assignMatch) {
      const variableName = assignMatch[3];
      const propertyName = variableToField.get(variableName);
      if (propertyName) {
        trackField(metadata, propertyName, (field) => {
          field.classField = assignMatch[2];
        });
      }
      continue;
    }
  }
  return metadata;
}

function inferTypeFromAccessor(accessor) {
  switch (accessor) {
    case "Int":
    case "Float":
    case "Double":
    case "Long":
    case "Short":
      return "number";
    case "Boolean":
      return "boolean";
    case "String":
      return "string";
    default:
      return undefined;
  }
}

function parseConstructorParameters(classSource, className) {
  const constructorRegex = new RegExp(`public\\s+${className}\\s*\\(([^)]*)\\)`);
  const match = constructorRegex.exec(classSource);
  if (!match) return [];
  const params = splitArguments(match[1]);
  return params.map((param) => {
    const parts = param.trim().split(/\s+/);
    return parts[parts.length - 1];
  });
}

function parseCreateDefaultInstance(serializerBody, className) {
  const body = extractMethodBodyByName(serializerBody, "createDefaultInstance");
  if (!body) return null;
  return body;
}

function parseDefaults(defaultBody, constructorParams, metadata, registryLookup) {
  if (!defaultBody) return;
  const returnIndex = defaultBody.indexOf("return");
  if (returnIndex === -1) return;
  const newIndex = defaultBody.indexOf("new", returnIndex);
  if (newIndex === -1) return;
  const openIndex = defaultBody.indexOf("(", newIndex);
  if (openIndex === -1) return;
  const segment = extractBalancedSegment(defaultBody, openIndex);
  if (!segment) return;
  const argsString = segment.content;
  const semicolonIndex = defaultBody.indexOf(";", segment.endIndex);
  const chainString = semicolonIndex === -1 ? "" : defaultBody.slice(segment.endIndex + 1, semicolonIndex);
  const args = splitArguments(argsString);
  args.forEach((arg, index) => {
    const paramName = constructorParams[index];
    if (!paramName) return;
    const fieldKey = toSnakeCase(paramName);
    if (!metadata.fields.has(fieldKey)) {
      trackField(metadata, fieldKey, () => {});
    }
    const field = metadata.fields.get(fieldKey);
    const expression = arg.trim();
    field.defaultExpression = expression;
    field.default = extractLiteralValue(expression, registryLookup);
  });
  const chainMatches = [...chainString.matchAll(/\.set([A-Za-z0-9_]+)\(([^)]+)\)/g)];
  for (const match of chainMatches) {
    const methodName = match[1];
    const fieldKey = toSnakeCase(methodName);
    const valueExpr = match[2].trim();
    if (!metadata.fields.has(fieldKey)) {
      trackField(metadata, fieldKey, () => {});
    }
    const field = metadata.fields.get(fieldKey);
    field.defaultExpression = valueExpr;
    field.default = extractLiteralValue(valueExpr, registryLookup);
  }
}

function extractLiteralValue(expression, registryLookup) {
  const trimmed = expression.trim();
  if (/^-?\d+(?:\.\d+)?f?$/.test(trimmed)) {
    const numeric = parseFloat(trimmed.replace(/f$/i, ""));
    return Number.isNaN(numeric) ? trimmed : numeric;
  }
  if (/^".*"$/.test(trimmed)) {
    return trimmed.slice(1, -1);
  }
  const instanceMatch = trimmed.match(/([A-Za-z0-9_\.]+)\.INSTANCE/);
  if (instanceMatch) {
    const className = instanceMatch[1];
    const registryEntry = registryLookup.get(className);
    if (registryEntry) {
      return registryEntry.id;
    }
    return className.split(".").pop();
  }
  const newMatch = trimmed.match(/new\s+([A-Za-z0-9_\.]+)\(/);
  if (newMatch) {
    const className = newMatch[1];
    const registryEntry = registryLookup.get(className);
    if (registryEntry) {
      return registryEntry.id;
    }
    return className.split(".").pop();
  }
  const enumMatch = trimmed.match(/([A-Za-z0-9_\.]+)\.([A-Z0-9_]+)/);
  if (enumMatch) {
    return enumMatch[2];
  }
  return trimmed;
}

function extractEnumDefinition(enumType) {
  const [outerName, innerName] = enumType.split(".");
  if (!outerName || !innerName) {
    return null;
  }
  const candidates = classIndex.bySimple.get(outerName);
  if (!candidates || candidates.length === 0) {
    return null;
  }
  const classInfo = candidates[0];
  const source = fs.readFileSync(classInfo.filePath, "utf8");
  const enumRegex = new RegExp(`enum\\s+${innerName}\\s*\\{([\\s\\S]*?)\\}`, "m");
  const match = enumRegex.exec(source);
  if (!match) {
    return null;
  }
  const enumBody = match[1];
  const constantsSection = enumBody.split(";")[0];
  const constants = splitEnumConstants(constantsSection);
  const values = [];
  for (const constant of constants) {
    const trimmed = constant.trim();
    if (!trimmed) continue;
    const nameMatch = trimmed.match(/^([A-Z0-9_]+)/);
    if (!nameMatch) continue;
    const id = nameMatch[1];
    const stringMatch = trimmed.match(/"([^"]+)"/);
    const value = stringMatch ? stringMatch[1] : null;
    const labelSource = value || id.toLowerCase();
    values.push({
      id,
      label: toLabel(labelSource.replace(/[_\-]+/g, " ")),
      value: value ?? null,
    });
  }
  if (values.length === 0) {
    return null;
  }
  return {
    type: enumType,
    source: classInfo.relativePath,
    values,
  };
}

function enumKeyFromType(enumType) {
  return enumType
    .replace(/[^A-Za-z0-9]+/g, "_")
    .replace(/^_+|_+$/g, "")
    .toLowerCase();
}

function gatherRegistryLookup(allRegistries) {
  const map = new Map();
  for (const registry of allRegistries) {
    for (const entry of registry.entries) {
      map.set(entry.classInfo.fullName, { registry: registry.registryId, id: entry.id });
      map.set(entry.classInfo.simpleName, { registry: registry.registryId, id: entry.id });
    }
  }
  return map;
}

function serialiseFields(fieldsMap) {
  const fields = [];
  for (const [name, data] of fieldsMap.entries()) {
    fields.push({
      name,
      type: data.type,
      optional: data.optional,
      classField: data.classField,
      jsonProperties: data.jsonProperties ? Array.from(data.jsonProperties) : undefined,
      default: data.default,
      defaultExpression: data.defaultExpression,
      helper: data.helper,
    });
  }
  fields.sort((a, b) => a.name.localeCompare(b.name));
  return fields;
}

function parseSerializer(entry, allRegistries) {
  const classSource = fs.readFileSync(entry.classInfo.filePath, "utf8");
  const serializerBody = extractSerializerBody(classSource);
  const serializeBody = extractJsonMethodBody(serializerBody, "serialize");
  const deserializeBody = extractJsonMethodBody(serializerBody, "deserialize");
  const constructorParams = parseConstructorParameters(classSource, entry.classInfo.simpleName);
  const metadata = parseSerializeMethod(serializeBody || "");
  parseDeserializeMethod(deserializeBody || "", metadata);
  const registryLookup = gatherRegistryLookup(allRegistries);
  const defaultBody = parseCreateDefaultInstance(serializerBody, entry.classInfo.simpleName);
  parseDefaults(defaultBody, constructorParams, metadata, registryLookup);
  const fields = serialiseFields(metadata.fields);
  return {
    className: entry.classInfo.fullName,
    source: entry.classInfo.relativePath,
    fields,
  };
}

function buildEnumMetadata(enumTypes) {
  const enums = {};
  if (!enumTypes || enumTypes.has("AttributeModifier.Operation")) {
    enums.attributeModifierOperation = {
      type: "AttributeModifier.Operation",
      values: [
        { id: "ADDITION", label: "Addition" },
        { id: "MULTIPLY_BASE", label: "Multiply Base" },
        { id: "MULTIPLY_TOTAL", label: "Multiply Total" },
      ],
    };
    if (enumTypes) {
      enumTypes.delete("AttributeModifier.Operation");
    }
  }
  if (enumTypes) {
    for (const enumType of enumTypes) {
      const definition = extractEnumDefinition(enumType);
      if (!definition) continue;
      const key = enumKeyFromType(enumType);
      enums[key] = definition;
    }
  }
  return enums;
}

function main() {
  const registries = registryConfigs.map(parseRegistry);
  const metadata = {
    generatedAt: new Date().toISOString(),
    registries: {},
    enums: {},
  };
  const enumTypes = new Set();
  for (const registry of registries) {
    const entries = {};
    for (const entry of registry.entries) {
      const serializerMetadata = parseSerializer(entry, registries);
      for (const field of serializerMetadata.fields) {
        if (field.type && field.type.startsWith("enum:")) {
          enumTypes.add(field.type.slice(5));
        }
      }
      entries[entry.id] = {
        id: entry.id,
        label: entry.label,
        className: serializerMetadata.className,
        source: serializerMetadata.source,
        fields: serializerMetadata.fields,
      };
    }
    metadata.registries[registry.key] = {
      id: registry.registryId,
      label: registry.label,
      source: registry.sourceFile,
      entries,
    };
  }
  metadata.enums = buildEnumMetadata(enumTypes);
  fs.writeFileSync(OUTPUT_PATH, JSON.stringify(metadata, null, 2));
  const totalEntries = registries.reduce((sum, registry) => sum + registry.entries.length, 0);
  console.log(`Wrote metadata for ${totalEntries} entries to ${path.relative(PROJECT_ROOT, OUTPUT_PATH)}`);
}

main();
