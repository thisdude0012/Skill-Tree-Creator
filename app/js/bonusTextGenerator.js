/**
 * Comprehensive Bonus Text Generator
 * Converts bonus JSON objects to human-readable text
 * Handles ALL bonus types with conditions, multipliers, and context
 */

const ATTRIBUTE_NAMES = {
  'minecraft:generic.max_health': 'Max Health',
  'minecraft:generic.follow_range': 'Follow Range',
  'minecraft:generic.knockback_resistance': 'Knockback Resistance',
  'minecraft:generic.movement_speed': 'Movement Speed',
  'minecraft:generic.flying_speed': 'Flying Speed',
  'minecraft:generic.attack_damage': 'Attack Damage',
  'minecraft:generic.attack_knockback': 'Attack Knockback',
  'minecraft:generic.attack_speed': 'Attack Speed',
  'minecraft:generic.armor': 'Armor',
  'minecraft:generic.armor_toughness': 'Armor Toughness',
  'minecraft:generic.luck': 'Luck',
  'minecraft:generic.jump_strength': 'Jump Strength',
  'minecraft:generic.fall_damage_multiplier': 'Fall Damage Multiplier',
  'minecraft:generic.safe_fall_distance': 'Safe Fall Distance',
  'minecraft:generic.scale': 'Scale',
  'minecraft:generic.step_height': 'Step Height',
  'minecraft:generic.gravity': 'Gravity',
  'minecraft:generic.reach_distance': 'Reach Distance',
  'minecraft:generic.burning_time': 'Burning Time',
  'minecraft:generic.explosion_knockback_resistance': 'Explosion Knockback Resistance',
  'minecraft:generic.mining_efficiency': 'Mining Efficiency',
  'minecraft:generic.air_sliding_friction': 'Air Sliding Friction',
  'minecraft:generic.water_movement_efficiency': 'Water Movement Efficiency',
  'minecraft:generic.movement_efficiency': 'Movement Efficiency',
  'minecraft:generic.oxygen_bonus': 'Oxygen Bonus',
  'minecraft:generic.entity_reach': 'Entity Reach',
  'minecraft:generic.block_interaction_range': 'Block Interaction Range',
  'minecraft:generic.block_break_speed': 'Block Break Speed'
};

const DAMAGE_CONDITION_NAMES = {
  'melee': 'melee',
  'ranged': 'ranged',
  'magic': 'magic',
  'explosion': 'explosion',
  'projectile': 'projectile',
  'poison': 'poison',
  'fire': 'fire',
  'none': '',
  'any': ''
};

const EFFECT_TYPE_NAMES = {
  'any': '',
  'beneficial': 'beneficial',
  'harmful': 'harmful',
  'neutral': 'neutral',
  'positive': 'positive',
  'negative': 'negative'
};

const MULTIPLIER_TYPE_NAMES = {
  'health_level': 'health',
  'armor_level': 'armor',
  'missing_health': 'missing health',
  'armor_toughness': 'armor toughness',
  'distance': 'block distance',
  'level': 'level',
  'experience': 'experience',
  'air': 'air',
  'hunger': 'hunger',
  'saturation': 'saturation'
};

const OPERATION_NAMES = {
  0: 'addition',
  1: 'multiply base',
  2: 'multiply total'
};

function formatPercentage(value) {
  if (value === 0) return '0%';
  const percentage = Math.round(value * 100);
  return `${percentage > 0 ? '+' : ''}${percentage}%`;
}

function formatNumber(value) {
  if (Number.isInteger(value)) {
    return `${value > 0 ? '+' : ''}${value}`;
  }
  const formatted = value.toFixed(2);
  return `${formatted.startsWith('-') ? '' : '+'}${formatted}`;
}

function getAttributeName(attributeId) {
  return ATTRIBUTE_NAMES[attributeId] || attributeId.split(':').pop().split('.').pop().replace(/_/g, ' ').replace(/\b\w/g, l => l.toUpperCase());
}

function getItemName(itemId) {
  if (!itemId) return 'item';
  return itemId.split(':').pop().replace(/_/g, ' ').replace(/\b\w/g, l => l.toUpperCase());
}

function getEffectName(effectId) {
  if (!effectId) return 'effect';
  return effectId.split(':').pop().replace(/_/g, ' ').replace(/\b\w/g, l => l.toUpperCase());
}

function formatAmount(amount, operation) {
  if (operation === 0) {
    return formatNumber(amount);
  } else {
    return formatPercentage(amount);
  }
}

function formatMultiplierType(type, target = 'player') {
  const name = MULTIPLIER_TYPE_NAMES[type] || type;
  return `${target} ${name}`;
}

function generateConditionText(bonus) {
  const conditions = [];
  
  // Player conditions
  if (bonus.player_condition && bonus.player_condition.type !== 'skilltree:none') {
    const condition = bonus.player_condition;
    switch (condition.type) {
      case 'skilltree:holding_item':
        if (condition.item) {
          conditions.push(`while holding ${getItemName(condition.item)}`);
        }
        break;
      case 'skilltree:biome':
        if (condition.biome) {
          conditions.push(`in ${condition.biome}`);
        }
        break;
      case 'skilltree:time':
        if (condition.time) {
          conditions.push(`during ${condition.time}`);
        }
        break;
      case 'skilltree:health_below':
        if (condition.value) {
          conditions.push(`while below ${Math.round(condition.value * 100)}% health`);
        }
        break;
      case 'skilltree:weather':
        if (condition.weather) {
          conditions.push(`during ${condition.weather}`);
        }
        break;
      default:
        conditions.push(`(player condition: ${condition.type.split(':').pop()})`);
    }
  }
  
  // Enemy conditions
  if (bonus.enemy_condition && bonus.enemy_condition.type !== 'skilltree:none') {
    const condition = bonus.enemy_condition;
    switch (condition.type) {
      case 'skilltree:entity_type':
        if (condition.entity) {
          conditions.push(`against ${condition.entity} enemies`);
        }
        break;
      default:
        conditions.push(`(enemy condition: ${condition.type.split(':').pop()})`);
    }
  }
  
  return conditions.length > 0 ? ` ${conditions.join(', ')}` : '';
}

function generateMultiplierText(bonus) {
  const multipliers = [];
  
  // Player multiplier
  if (bonus.player_multiplier && bonus.player_multiplier.type !== 'skilltree:none') {
    const mult = bonus.player_multiplier;
    const multiplierName = formatMultiplierType(mult.type, 'player');
    const amount = mult.amount || 1;
    multipliers.push(`per ${amount} ${multiplierName}`);
  }
  
  // Enemy multiplier
  if (bonus.enemy_multiplier && bonus.enemy_multiplier.type !== 'skilltree:none') {
    const mult = bonus.enemy_multiplier;
    const multiplierName = formatMultiplierType(mult.type, 'enemy');
    const amount = mult.amount || 1;
    multipliers.push(`per ${amount} ${multiplierName}`);
  }
  
  return multipliers.length > 0 ? ` ${multipliers.join(', ')}` : '';
}

// Specific bonus type generators
function generateDamageText(bonus) {
  const amount = bonus.amount || 0;
  const operation = bonus.operation || 0;
  const damageCondition = bonus.damage_condition || 'none';
  
  let text = formatAmount(amount, operation);
  text += ' damage';
  
  const conditionName = DAMAGE_CONDITION_NAMES[damageCondition];
  if (conditionName && conditionName !== '') {
    text = `${conditionName} ${text}`;
  }
  
  return text;
}

function generateAttributeText(bonus) {
  const amount = bonus.amount || 0;
  const operation = bonus.operation || 0;
  const attribute = bonus.attribute || '';
  
  const attributeName = getAttributeName(attribute);
  const formattedAmount = formatAmount(amount, operation);
  
  return `${formattedAmount} ${attributeName}`;
}

function generateCritChanceText(bonus) {
  const chance = bonus.chance || 0;
  return `${formatPercentage(chance)} critical hit chance`;
}

function generateCritDamageText(bonus) {
  const damage = bonus.damage || 0;
  return `${formatPercentage(damage)} critical hit damage`;
}

function generateDamageTakenText(bonus) {
  const amount = bonus.amount || 0;
  const operation = bonus.operation || 0;
  
  let text = formatAmount(amount, operation);
  text += ' damage taken';
  
  if (bonus.damage_condition && bonus.damage_condition !== 'none') {
    const condition = DAMAGE_CONDITION_NAMES[bonus.damage_condition];
    if (condition) {
      text = `${condition} ${text}`;
    }
  }
  
  return text;
}

function generateFreeEnchantmentText(bonus) {
  const enchantment = bonus.enchantment || 'unknown';
  const level = bonus.level || 1;
  const enchantmentName = enchantment.split(':').pop().replace(/_/g, ' ');
  
  return `Free ${enchantmentName} ${level}`;
}

function generateArrowRetrievalText(bonus) {
  const chance = bonus.chance || 0;
  return `${formatPercentage(chance)} arrow retrieval chance`;
}

function generateGainedExperienceText(bonus) {
  const amount = bonus.amount || 0;
  const operation = bonus.operation || 0;
  const source = bonus.source || 'mobs';
  
  let text = formatAmount(amount, operation);
  text += ' experience';
  
  if (source && source !== 'none') {
    text += ` from ${source}`;
  }
  
  return text;
}

function generateHealingText(bonus) {
  const amount = bonus.amount || 0;
  const operation = bonus.operation || 0;
  
  let text = formatAmount(amount, operation);
  text += ' healing';
  
  if (bonus.source) {
    text += ` from ${bonus.source}`;
  }
  
  return text;
}

function generateDamageConversionText(bonus) {
  const from = bonus.from || 'unknown';
  const to = bonus.to || 'unknown';
  const amount = bonus.amount || 0;
  
  return `Convert ${formatPercentage(amount)} ${from} damage to ${to} damage`;
}

function generateInflictDamageText(bonus) {
  const amount = bonus.amount || 0;
  const operation = bonus.operation || 0;
  const damageType = bonus.damage_type || 'unknown';
  
  let text = formatAmount(amount, operation);
  text += ` ${damageType} damage`;
  
  if (bonus.target) {
    text += ` to ${bonus.target}`;
  }
  
  return text;
}

function generateInflictEffectText(bonus) {
  const effect = bonus.effect || 'unknown';
  const duration = bonus.duration || 0;
  const amplifier = bonus.amplifier || 0;
  const target = bonus.target || 'enemy';
  
  const effectName = getEffectName(effect);
  let text = `Inflict ${effectName}`;
  
  if (amplifier > 0) {
    text += ` ${amplifier}`;
  }
  
  if (duration > 0) {
    text += ` for ${duration}s`;
  }
  
  if (target) {
    text += ` on ${target}s`;
  }
  
  return text;
}

function generateGrantItemText(bonus) {
  const item = bonus.item || 'unknown';
  const count = bonus.count || 1;
  const itemName = getItemName(item);
  
  return `+${count}x ${itemName}`;
}

function generateLootDuplicationText(bonus) {
  const chance = bonus.chance || 0;
  const lootType = bonus.loot_type || 'any';
  const multiplier = bonus.multiplier || 2;
  
  const chanceText = formatPercentage(chance);
  const multiplierText = multiplier === 2 ? 'double' : `${multiplier}x`;
  
  let text = `${chanceText} chance for ${multiplierText} loot`;
  
  if (lootType && lootType !== 'any') {
    text += ` (${lootType})`;
  }
  
  return text;
}

function generateEffectDurationText(bonus) {
  const multiplier = bonus.multiplier || 1;
  const effectType = bonus.effect_type || 'any';
  const typeName = EFFECT_TYPE_NAMES[effectType];
  
  const bonusPercent = formatPercentage(multiplier - 1);
  let text = `${bonusPercent} effect duration`;
  
  if (typeName && typeName !== '') {
    text += ` (${typeName} effects)`;
  }
  
  return text;
}

function generateCuriosSlotText(bonus) {
  const slot = bonus.slot || 'unknown';
  const count = bonus.count || 1;
  
  return `+${count} ${slot} curios slot${count !== 1 ? 's' : ''}`;
}

function generateSkillPointText(bonus) {
  const amount = bonus.amount || 0;
  return `+${amount} skill point${amount !== 1 ? 's' : ''}`;
}

function generateResistEffectText(bonus) {
  const effect = bonus.effect || 'unknown';
  const effectName = getEffectName(effect);
  return `Resist ${effectName} effects`;
}

function generateJumpHeightText(bonus) {
  const amount = bonus.amount || 0;
  const operation = bonus.operation || 0;
  return `${formatAmount(amount, operation)} jump height`;
}

function generateReachDistanceText(bonus) {
  const amount = bonus.amount || 0;
  const operation = bonus.operation || 0;
  return `${formatAmount(amount, operation)} reach distance`;
}

function generateMiningSpeedText(bonus) {
  const amount = bonus.amount || 0;
  const operation = bonus.operation || 0;
  return `${formatAmount(amount, operation)} mining speed`;
}

function generateSwimSpeedText(bonus) {
  const amount = bonus.amount || 0;
  const operation = bonus.operation || 0;
  return `${formatAmount(amount, operation)} swim speed`;
}

function generateFlightSpeedText(bonus) {
  const amount = bonus.amount || 0;
  const operation = bonus.operation || 0;
  return `${formatAmount(amount, operation)} flight speed`;
}

function generateFallDamageText(bonus) {
  const amount = bonus.amount || 0;
  const operation = bonus.operation || 0;
  return `${formatAmount(amount, operation)} fall damage`;
}

function generateKnockbackText(bonus) {
  const amount = bonus.amount || 0;
  const operation = bonus.operation || 0;
  return `${formatAmount(amount, operation)} knockback`;
}

function generateLuckText(bonus) {
  const amount = bonus.amount || 0;
  const operation = bonus.operation || 0;
  return `${formatAmount(amount, operation)} luck`;
}

// Main generator function
export function generateBonusText(bonus) {
  if (!bonus || typeof bonus !== 'object') {
    return 'Unknown bonus';
  }
  
  const type = bonus.type || 'skilltree:none';
  
  let text = '';
  
  switch (type) {
    case 'skilltree:damage':
      text = generateDamageText(bonus);
      break;
      
    case 'skilltree:attribute':
      text = generateAttributeText(bonus);
      break;
      
    case 'skilltree:crit_chance':
      text = generateCritChanceText(bonus);
      break;
      
    case 'skilltree:crit_damage':
      text = generateCritDamageText(bonus);
      break;
      
    case 'skilltree:damage_taken':
      text = generateDamageTakenText(bonus);
      break;
      
    case 'skilltree:free_enchantment':
      text = generateFreeEnchantmentText(bonus);
      break;
      
    case 'skilltree:arrow_retrieval':
      text = generateArrowRetrievalText(bonus);
      break;
      
    case 'skilltree:experience':
    case 'skilltree:gained_experience':
      text = generateGainedExperienceText(bonus);
      break;
      
    case 'skilltree:healing':
      text = generateHealingText(bonus);
      break;
      
    case 'skilltree:damage_conversion':
      text = generateDamageConversionText(bonus);
      break;
      
    case 'skilltree:inflict_damage':
      text = generateInflictDamageText(bonus);
      break;
      
    case 'skilltree:inflict_effect':
      text = generateInflictEffectText(bonus);
      break;
      
    case 'skilltree:grant_item':
      text = generateGrantItemText(bonus);
      break;
      
    case 'skilltree:loot_duplication':
      text = generateLootDuplicationText(bonus);
      break;
      
    case 'skilltree:effect_duration':
      text = generateEffectDurationText(bonus);
      break;
      
    case 'skilltree:curios_slot':
      text = generateCuriosSlotText(bonus);
      break;
      
    case 'skilltree:skill_point':
      text = generateSkillPointText(bonus);
      break;
      
    case 'skilltree:resist_effect':
      text = generateResistEffectText(bonus);
      break;
      
    case 'skilltree:jump_height':
      text = generateJumpHeightText(bonus);
      break;
      
    case 'skilltree:reach_distance':
      text = generateReachDistanceText(bonus);
      break;
      
    case 'skilltree:mining_speed':
      text = generateMiningSpeedText(bonus);
      break;
      
    case 'skilltree:swim_speed':
      text = generateSwimSpeedText(bonus);
      break;
      
    case 'skilltree:flight_speed':
      text = generateFlightSpeedText(bonus);
      break;
      
    case 'skilltree:fall_damage':
      text = generateFallDamageText(bonus);
      break;
      
    case 'skilltree:knockback':
      text = generateKnockbackText(bonus);
      break;
      
    case 'skilltree:luck':
      text = generateLuckText(bonus);
      break;
      
    default:
      // Fallback to generic text
      const bonusType = type.split(':').pop().replace(/_/g, ' ');
      text = `${bonusType.charAt(0).toUpperCase() + bonusType.slice(1)} bonus`;
  }
  
  // Append conditions and multipliers
  text += generateConditionText(bonus);
  text += generateMultiplierText(bonus);
  
  return text;
}

export function generateBonusSummary(bonus) {
  // This function is now just a wrapper around generateBonusText
  // since we've integrated all the logic into the main function
  return generateBonusText(bonus);
}