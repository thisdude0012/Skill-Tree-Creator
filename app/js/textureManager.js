const TEXTURE_PATHS = {
  background: [
    "class.png",
    "gateway.png",
    "keystone.png",
    "lesser.png",
    "notable.png",
    "recipe.png",
  ],
  icon: [
    "apple_green.png",
    "arrow.png",
    "arrow_bronze.png",
    "arrow_diamond.png",
    "arrow_emerald.png",
    "arrow_steel.png",
    "bone.png",
    "boots_bronze.png",
    "boots_gold.png",
    "boots_leather.png",
    "bow_diamond.png",
    "bow_emerald.png",
    "bow_gold.png",
    "chestplate_bronze_fur.png",
    "chestplate_fur.png",
    "chestplate_leather.png",
    "chicken.png",
    "chicken_leg.png",
    "chili_pepper_red.png",
    "cookie.png",
    "cross_green.png",
    "cross_red.png",
    "eye_green.png",
    "fishing_rod_diamond.png",
    "fishing_rod_emerald.png",
    "fishing_rod_gold.png",
    "fishing_rod_steel.png",
    "glove_bronze.png",
    "glove_diamond.png",
    "glove_emerald.png",
    "glove_gold.png",
    "glove_iron.png",
    "glove_steel.png",
    "heart_black.png",
    "heart_cyan.png",
    "heart_green.png",
    "heart_red.png",
    "heart_violet.png",
    "heart_yellow.png",
    "helmet_leather.png",
    "meal.png",
    "pants_gold.png",
    "pie.png",
    "pie_cream.png",
    "potion_black.png",
    "potion_black_big.png",
    "potion_black_small.png",
    "potion_blue.png",
    "potion_blue_small.png",
    "potion_brown.png",
    "potion_cyan.png",
    "potion_cyan_big.png",
    "potion_double.png",
    "potion_gray.png",
    "potion_gray_big.png",
    "potion_gray_small.png",
    "potion_green.png",
    "potion_green_big.png",
    "potion_green_small.png",
    "potion_indigo.png",
    "potion_indigo_small.png",
    "potion_pink_big.png",
    "potion_red.png",
    "potion_red_big.png",
    "potion_white.png",
    "potion_white_big.png",
    "potion_white_small.png",
    "potion_yellow_big.png",
    "skull.png",
    "soup_red.png",
    "soup_yellow.png",
    "sword_bronze.png",
    "sword_gold.png",
    "sword_iron.png",
    "torch.png",
    "treasure_chest.png",
    "treasure_chest_gold.png",
    "void.png",
  ],
  border: [
    "gateway.png",
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
      console.warn('Unknown texture type:', textureType, 'for select:', select);
      return;
    }
    
    select.innerHTML = '<option value="">Select...</option>';
    
    TEXTURE_PATHS[textureType].forEach((filename) => {
      const option = document.createElement("option");
      option.value = getTexturePath(textureType, filename);
      option.textContent = filename.replace(".png", "");
      
      // Add data attribute for preview path
      const previewPath = getTextureDisplayPath(textureType, filename);
      option.dataset.previewPath = previewPath;
      
      select.appendChild(option);
    });
    
    select.addEventListener("change", (e) => {
      updateTexturePreview(field, e.target.value);
    });
  });
}

function convertTexturePathToDisplay(texturePath) {
  if (!texturePath) return '';
  
  // If it's already a display path, return as-is
  if (texturePath.startsWith('./assets/')) {
    return texturePath;
  }
  
  // Convert skilltree: paths to display paths
  if (texturePath.startsWith("skilltree:")) {
    // Extract texture type and filename from the skilltree: path
    const pathMatch = texturePath.match(/skilltree:textures\/([^\/]+)\/(.+)/);
    if (pathMatch) {
      const [, subfolder, filename] = pathMatch;
      return `./assets/skilltree/textures/${subfolder}/${filename}`;
    } else {
      // Fallback: just replace the prefix
      return texturePath.replace("skilltree:", "./assets/skilltree/");
    }
  }
  
  // Return as-is if no conversion needed
  return texturePath;
}

function updateTexturePreview(field, texturePath) {
  const preview = document.querySelector(`[data-texture-preview="${field}"]`);
  if (!preview) return;
  
  preview.innerHTML = "";
  
  if (!texturePath) return;
  
  const parts = texturePath.split("/");
  const filename = parts[parts.length - 1];
  
  // Convert to display path
  const displayPath = convertTexturePathToDisplay(texturePath);
  
  // Debug logging
  console.log('Texture preview update:', {
    field,
    originalPath: texturePath,
    displayPath,
    filename
  });
  
  const img = document.createElement("img");
  img.src = displayPath;
  img.alt = filename;
  img.onerror = () => {
    console.warn('Texture not found:', displayPath);
    preview.innerHTML = "";
    const fallback = document.createElement("span");
    fallback.textContent = "?";
    fallback.style.color = "var(--color-text-muted)";
    fallback.style.fontSize = "1.2rem";
    fallback.style.fontWeight = "bold";
    fallback.style.display = "inline-block";
    fallback.style.width = "20px";
    fallback.style.height = "20px";
    fallback.style.lineHeight = "20px";
    fallback.style.textAlign = "center";
    fallback.style.border = "1px dashed var(--color-border)";
    fallback.style.borderRadius = "2px";
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
  getTexturePath,
  getTextureDisplayPath,
  getTexturePathFromFilename,
  convertTexturePathToDisplay,
  TEXTURE_PATHS,
};
