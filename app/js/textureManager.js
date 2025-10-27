const TEXTURE_PATHS = {
  background: [
    "class.png",
    "greater.png",
    "keystone.png",
    "lesser.png",
    "notable.png",
  ],
  icon: [
    "apple_green.png",
    "apple_red.png",
    "arrow.png",
    "arrow_diamond.png",
    "axe_diamond.png",
    "axe_gold.png",
    "axe_iron.png",
    "axe_steel.png",
    "axe_stone.png",
    "axe_wood.png",
    "boots_diamond.png",
    "boots_gold.png",
    "boots_iron.png",
    "boots_leather.png",
    "boots_steel.png",
    "bone.png",
    "bow_diamond.png",
    "bow_gold.png",
    "bow_iron.png",
    "chestplate_diamond.png",
    "chestplate_gold.png",
    "chestplate_iron.png",
    "chestplate_leather.png",
    "chestplate_steel.png",
    "cross_green.png",
    "cross_red.png",
    "fishing_rod_diamond.png",
    "fishing_rod_gold.png",
    "fishing_rod_iron.png",
    "glove_bronze.png",
    "glove_diamond.png",
    "glove_emerald.png",
    "glove_iron.png",
    "glove_silver.png",
    "glove_steel.png",
    "heart_green.png",
    "heart_red.png",
    "helmet_diamond.png",
    "helmet_gold.png",
    "helmet_iron.png",
    "helmet_leather.png",
    "helmet_steel.png",
    "hoe_diamond.png",
    "hoe_gold.png",
    "hoe_iron.png",
    "pants_diamond.png",
    "pants_gold.png",
    "pants_iron.png",
    "pants_leather.png",
    "pants_steel.png",
    "pickaxe_diamond.png",
    "pickaxe_gold.png",
    "pickaxe_iron.png",
    "pickaxe_steel.png",
    "pickaxe_stone.png",
    "pickaxe_wood.png",
    "potion_black_big.png",
    "potion_blue.png",
    "potion_blue_big.png",
    "potion_cyan_big.png",
    "potion_double.png",
    "potion_gray_big.png",
    "potion_green_big.png",
    "potion_green_small.png",
    "potion_orange_big.png",
    "potion_pink_big.png",
    "potion_purple_big.png",
    "potion_red_big.png",
    "potion_yellow_big.png",
    "shovel_diamond.png",
    "shovel_gold.png",
    "shovel_iron.png",
    "shovel_steel.png",
    "shovel_stone.png",
    "shovel_wood.png",
    "soup_brown.png",
    "soup_green.png",
    "soup_red.png",
    "soup_yellow.png",
    "sword_diamond.png",
    "sword_gold.png",
    "sword_iron.png",
    "sword_steel.png",
    "sword_stone.png",
    "sword_wood.png",
    "treasure_chest.png",
    "void.png",
  ],
  border: [
    "greater.png",
    "keystone.png",
    "lesser.png",
    "notable.png",
  ],
};

function getTexturePath(type, filename) {
  const subfolders = {
    background: "icons/background",
    icon: "icons",
    border: "tooltip",
  };
  
  const subfolder = subfolders[type] || "icons";
  return `skilltree:textures/${subfolder}/${filename}`;
}

function getTextureDisplayPath(type, filename) {
  const subfolders = {
    background: "icons/background",
    icon: "icons",
    border: "tooltip",
  };
  
  const subfolder = subfolders[type] || "icons";
  return `./assets/skilltree/textures/${subfolder}/${filename}`;
}

function initializeTextureDropdowns() {
  const textureSelects = document.querySelectorAll(".texture-select");
  
  textureSelects.forEach((select) => {
    const textureType = select.dataset.textureType;
    const field = select.dataset.field;
    
    if (!textureType || !TEXTURE_PATHS[textureType]) {
      return;
    }
    
    select.innerHTML = '<option value="">Select...</option>';
    
    TEXTURE_PATHS[textureType].forEach((filename) => {
      const option = document.createElement("option");
      option.value = getTexturePath(textureType, filename);
      option.textContent = filename.replace(".png", "");
      select.appendChild(option);
    });
    
    select.addEventListener("change", (e) => {
      updateTexturePreview(field, e.target.value);
    });
  });
}

function updateTexturePreview(field, texturePath) {
  const preview = document.querySelector(`[data-texture-preview="${field}"]`);
  if (!preview) return;
  
  preview.innerHTML = "";
  
  if (!texturePath) return;
  
  const parts = texturePath.split("/");
  const filename = parts[parts.length - 1];
  
  let displayPath = texturePath;
  if (texturePath.startsWith("skilltree:")) {
    displayPath = texturePath.replace("skilltree:", "./assets/skilltree");
  }
  
  const img = document.createElement("img");
  img.src = displayPath;
  img.alt = filename;
  img.onerror = () => {
    preview.innerHTML = "";
    const fallback = document.createElement("span");
    fallback.textContent = "?";
    fallback.style.color = "var(--color-text-muted)";
    fallback.style.fontSize = "1.2rem";
    preview.appendChild(fallback);
  };
  
  preview.appendChild(img);
}

function getTexturePathFromFilename(textureType, filename) {
  return getTexturePath(textureType, filename);
}

export {
  initializeTextureDropdowns,
  updateTexturePreview,
  getTexturePathFromFilename,
  TEXTURE_PATHS,
};
