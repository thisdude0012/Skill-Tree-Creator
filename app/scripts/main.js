const dom = {
  root: document.documentElement,
  navToggle: document.querySelector('[data-nav-toggle]'),
  nav: document.querySelector('[data-nav]'),
  navLinks: [],
  sections: Array.from(document.querySelectorAll('[data-section]')),
  preview: document.querySelector('[data-preview]'),
  placeholders: Array.from(document.querySelectorAll('[data-placeholder]')),
};

dom.navLinks = dom.nav ? Array.from(dom.nav.querySelectorAll('[data-nav-link]')) : [];

dom.root.classList.add('theme-dark');

dom.sections.forEach((section) => {
  section.dataset.ready = 'false';
});

const state = {
  activeSectionId: dom.sections[0] ? dom.sections[0].id : null,
  navOpen: false,
};

function setActiveSection(sectionId) {
  if (!sectionId) return;
  const section = dom.sections.find((entry) => entry.id === sectionId);
  if (!section) return;

  state.activeSectionId = sectionId;
  dom.sections.forEach((entry) => {
    entry.dataset.ready = entry.id === sectionId ? 'true' : entry.dataset.ready;
  });

  dom.navLinks.forEach((link) => {
    const href = link.getAttribute('href') || '';
    const targetId = href.startsWith('#') ? href.slice(1) : href;
    const isActive = targetId === sectionId;
    link.classList.toggle('is-active', isActive);
    link.setAttribute('aria-current', isActive ? 'location' : 'false');
  });
}

function closeNav() {
  if (!dom.nav || !dom.navToggle) return;
  dom.nav.classList.remove('is-open');
  dom.navToggle.setAttribute('aria-expanded', 'false');
  state.navOpen = false;
}

function openNav() {
  if (!dom.nav || !dom.navToggle) return;
  dom.nav.classList.add('is-open');
  dom.navToggle.setAttribute('aria-expanded', 'true');
  state.navOpen = true;
}

function toggleNav() {
  if (!dom.nav || !dom.navToggle) return;
  if (state.navOpen) {
    closeNav();
  } else {
    openNav();
  }
}

function setupNav() {
  if (!dom.nav || !dom.navToggle) return;
  dom.navToggle.addEventListener('click', toggleNav);

  dom.navLinks.forEach((link) => {
    link.addEventListener('click', () => {
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

  window.addEventListener('resize', () => {
    if (window.innerWidth >= 768) {
      openNav();
    } else {
      closeNav();
    }
  });

  document.addEventListener('keydown', (event) => {
    if (event.key === 'Escape' && state.navOpen && window.innerWidth < 768) {
      closeNav();
    }
  });
}

function observeSections() {
  if (!('IntersectionObserver' in window) || dom.sections.length === 0) {
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
      rootMargin: '-45% 0px -45%',
      threshold: [0.25, 0.6, 1],
    }
  );

  dom.sections.forEach((section) => observer.observe(section));
}

function hydratePlaceholders() {
  dom.placeholders.forEach((element) => {
    element.dataset.hydrated = 'false';
  });
}

function syncHashWithActiveSection() {
  const hash = window.location.hash.replace('#', '');
  if (!hash) {
    setActiveSection(state.activeSectionId);
    return;
  }

  const section = dom.sections.find((entry) => entry.id === hash);
  if (section) {
    setActiveSection(section.id);
    section.scrollIntoView({ behavior: 'smooth', block: 'start' });
  }
}

setupNav();
observeSections();
hydratePlaceholders();
syncHashWithActiveSection();

window.addEventListener('hashchange', () => {
  syncHashWithActiveSection();
  if (state.navOpen && window.innerWidth < 768) {
    closeNav();
  }
});

export { dom, state, setActiveSection };

window.SkillCreatorShell = {
  dom,
  state,
  setActiveSection,
  toggleNav,
};
