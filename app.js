// import experiences from './experiences.json'
// import projects from './projects.json'
import { marked } from 'https://cdn.jsdelivr.net/npm/marked/lib/marked.esm.js';

let projectData = []
document.addEventListener('DOMContentLoaded', async () => {
    await loadProjects();

    const projectList = document.querySelector('#project-list');
    const overlay = document.querySelector('#modal-overlay');
    const closeBtn = document.querySelector('#modal-close');

    projectList.addEventListener('click', (e) => {
        const item = e.target.closest('.project-item');
        if (!item) return;

        const projectId = item.dataset.projectId;

        const project = projectData.find(p => p.id == projectId);
        if (!project) return;

        openModal(project);
    });

    closeBtn.addEventListener('click', (e) => {
        closeModal()
    })
    // overlay.onclick = (e) => {
    //     if (e.target === overlay) closeModal();
    // };
});

async function loadProjects() {
    const res = await fetch('./projects.json');
    projectData = await res.json();

    const projectList = document.querySelector('#project-list');

    projectList.innerHTML = projectData.map(project => `
    <div class="project-item" data-project-id="${project.id}">
        <img class="project-preview" src="${project.assests[0].path}">
        <div class="project-name">${project.name}</div>
    </div>
    `).join('');
}

function openModal(project) {
    document.body.style.overflow = 'hidden';

    document.querySelector('#modal-title').textContent = project.name;
    document.querySelector('#modal-period').textContent = project.period;
    document.querySelector('#modal-desc').textContent = project.descriptions;
    const imagesContainer = document.querySelector('#modal-images');
    imagesContainer.innerHTML = project.assests
        .map(img => `<img src="${img.path}" alt="">`)
        .join('');
    document.querySelector('#modal-overlay').classList.add('active');

    document.querySelector('#modal-desc').innerHTML =
        marked.parse(project.descriptions);
}

function closeModal() {
    document.body.style.overflow = '';
    document.querySelector('#modal-overlay').classList.remove('active');
}





