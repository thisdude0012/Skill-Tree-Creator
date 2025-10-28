/**
 * Bonus Text Generator
 * Converts bonus JSON objects to human-readable text
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
  'any': ''
};

const EFFECT_TYPE_NAMES = {
  'any': '',
  'beneficial': 'beneficial',
  'harmful': 'harmful',
  'neutral': 'neutral'
};

const OPERATION_NAMES = {
  0: 'addition',
  1: 'multiply base',
  2: 'multiply total'
};

function formatPercentage(value) {
  if (value === 0) return '0%';
  const percentage = Math.round(value * 100);
  return `${percentage}%`;
}

function formatNumber(value) {
  if (Number.isInteger(value)) {
    return value.toString();
  }
  return value.toFixed(2);
}

function getAttributeName(attributeId) {
  return ATTRIBUTE_NAMES[attributeId] || attributeId.split(':').pop().split('.').pop().replace(/_/g, ' ').replace(/\b\w/g, l => l.toUpperCase());
}

function generateDamageBonusText(bonus) {
  const amount = bonus.amount || 0;
  const operation = bonus.operation || 0;
  const damageCondition = bonus.damage_condition || 'any';
  
  let text = '';
  
  if (operation === 0) {
    text = `+${formatNumber(amount)} damage`;
  } else {
    text = `+${formatPercentage(amount)} damage`;
  }
  
  const conditionName = DAMAGE_CONDITION_NAMES[damageCondition];
  if (conditionName) {
    text += ` (${conditionName})`;
  }
  
  return text;
}

function generateAttributeBonusText(bonus) {
  const amount = bonus.amount || 0;
  const operation = bonus.operation || 0;
  const attribute = bonus.attribute || '';
  
  const attributeName = getAttributeName(attribute);
  
  if (operation === 0) {
    return `+${formatNumber(amount)} ${attributeName}`;
  } else if (operation === 1) {
    return `+${formatPercentage(amount)} ${attributeName}`;
  } else {
    return `+${formatPercentage(amount)} ${attributeName} (multiplicative)`;
  }
}

function generateCritBonusText(bonus) {
  if (bonus.chance !== undefined) {
    return `+${formatPercentage(bonus.chance)} critical hit chance`;
  }
  if (bonus.damage !== undefined) {
    return `+${formatPercentage(bonus.damage)} critical hit damage`;
  }
  return 'Critical hit bonus';
}

function generateEffectDurationText(bonus) {
  const multiplier = bonus.multiplier || 1;
  const effectType = bonus.effect_type || 'any';
  const typeName = EFFECT_TYPE_NAMES[effectType];
  
  const bonusPercent = formatPercentage(multiplier - 1);
  let text = `+${bonusPercent} effect duration`;
  
  if (typeName) {
    text += ` (${typeName} effects)`;
  }
  
  return text;
}

function generateGrantItemText(bonus) {
  const item = bonus.item || 'item';
  const count = bonus.count || 1;
  const itemName = item.split(':').pop().replace(/_/g, ' ').replace(/\b\w/g, l => l.toUpperCase());
  
  return `+${count}x ${itemName}`;
}

function generateInflictEffectText(bonus) {
  const effect = bonus.effect || 'effect';
  const duration = bonus.duration || 0;
  const amplifier = bonus.amplifier || 0;
  const target = bonus.target || 'enemy';
  
  const effectName = effect.split(':').pop().replace(/_/g, ' ').replace(/\b\w/g, l => l.toUpperCase());
  const targetName = target === 'player' ? 'self' : 'enemies';
  
  let text = `Inflict ${effectName}`;
  if (amplifier > 0) {
    text += ` ${amplifier}`;
  }
  text += ` on ${targetName}`;
  
  if (duration > 0) {
    text += ` for ${duration}s`;
  }
  
  return text;
}

function generateLootDuplicationText(bonus) {
  const chance = bonus.chance || 0;
  const lootType = bonus.loot_type || 'any';
  const multiplier = bonus.multiplier || 2;
  
  const chanceText = formatPercentage(chance);
  const multiplierText = multiplier === 2 ? 'double' : `${multiplier}x`;
  
  let text = `${chanceText} chance for ${multiplierText} loot`;
  
  if (lootType !== 'any') {
    text += ` (${lootType})`;
  }
  
  return text;
}

function generateExperienceText(bonus) {
  const source = bonus.source || 'mobs';
  const multiplier = bonus.multiplier || 1;
  
  const bonusPercent = formatPercentage(multiplier - 1);
  return `+${bonusPercent} experience from ${source}`;
}

export function generateBonusText(bonus) {
  if (!bonus || typeof bonus !== 'object') {
    return 'Unknown bonus';
  }
  
  const type = bonus.type || 'skilltree:none';
  
  switch (type) {
    case 'skilltree:damage':
      return generateDamageBonusText(bonus);
    
    case 'skilltree:attribute':
      return generateAttributeBonusText(bonus);
    
    case 'skilltree:crit_chance':
    case 'skilltree:crit_damage':
      return generateCritBonusText(bonus);
    
    case 'skilltree:effect_duration':
      return generateEffectDurationText(bonus);
    
    case 'skilltree:grant_item':
      return generateGrantItemText(bonus);
    
    case 'skilltree:inflict_effect':
      return generateInflictEffectText(bonus);
    
    case 'skilltree:loot_duplication':
      return generateLootDuplicationText(bonus);
    
    case 'skilltree:experience':
      return generateExperienceText(bonus);
    
    default:
      // Fallback to generic text
      const bonusType = type.split(':').pop().replace(/_/g, ' ');
      return `${bonusType.charAt(0).toUpperCase() + bonusType.slice(1)} bonus`;
  }
}

export function generateBonusSummary(bonus) {
  const text = generateBonusText(bonus);
  
  // Add conditional information if present
  const conditions = [];
  
  if (bonus.conditions) {
    if (bonus.conditions.holding) {
      conditions.push(`while holding ${bonus.conditions.holding}`);
    }
    if (bonus.conditions.biome) {
      conditions.push(`in ${bonus.conditions.biome}`);
    }
    if (bonus.conditions.time) {
      conditions.push(`during ${bonus.conditions.time}`);
    }
    if (bonus.conditions.health_below) {
      conditions.push(`while below ${bonus.conditions.health_below}% health`);
    }
  }
  
  if (conditions.length > 0) {
    return `${text} ${conditions.join(', ')}`;
  }
  
  return text;
}