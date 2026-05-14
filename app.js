// import experiences from './experiences.json'
// import projects from './projects.json'
import { marked } from 'https://cdn.jsdelivr.net/npm/marked/lib/marked.esm.js';

let projectData = []
const MAILERSEND_API_URL = 'https://api.mailersend.com/v1/email';
const mailerConfig = {
    apiToken: '', // TODO: add your token here or inject from backend
    fromEmail: 'ducnv@test-r9084zvnyzvgw63d.mlsender.net',
    toEmail: 'ducnv2411150@usth.edu.vn'
};

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
    });

    const contactForm = document.querySelector('#contactForm');
    if (contactForm) {
        contactForm.addEventListener('submit', handleContactSubmit);
    }
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

async function handleContactSubmit(event) {
    event.preventDefault();

    const form = event.currentTarget;
    const submitButton = document.querySelector('#contactSubmitBtn');
    const name = document.querySelector('#name')?.value.trim() || '';
    const email = document.querySelector('#email')?.value.trim() || '';
    const message = document.querySelector('#message')?.value.trim() || '';

    if (!name || !email || !message) {
        showFormMessage('Vui long dien day du thong tin.', 'error');
        return;
    }

    if (!isValidEmail(email)) {
        showFormMessage('Email khong hop le.', 'error');
        return;
    }

    if (!mailerConfig.apiToken) {
        showFormMessage('Chua cau hinh MailerSend API token trong app.js.', 'error');
        return;
    }

    const payload = {
        from: {
            email: mailerConfig.fromEmail
        },
        to: [
            {
                email: mailerConfig.toEmail
            }
        ],
        subject: `Portfolio contact from ${name}`,
        text: `Sender: ${name} <${email}>\n\nMessage:\n${message}`,
        html: `<p><strong>Sender:</strong> ${escapeHtml(name)} &lt;${escapeHtml(email)}&gt;</p><p><strong>Message:</strong></p><p>${escapeHtml(message).replace(/\n/g, '<br>')}</p>`
    };

    try {
        if (submitButton) submitButton.disabled = true;

        const response = await fetch(MAILERSEND_API_URL, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'X-Requested-With': 'XMLHttpRequest',
                'Authorization': `Bearer ${mailerConfig.apiToken}`
            },
            body: JSON.stringify(payload)
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`MailerSend error ${response.status}: ${errorText}`);
        }

        showFormMessage('Gui thanh cong. Cam on ban da lien he!', 'success');
        form.reset();
    } catch (error) {
        console.error(error);
        showFormMessage('Gui that bai. Vui long thu lai sau.', 'error');
    } finally {
        if (submitButton) submitButton.disabled = false;
    }
}

function showFormMessage(message, type) {
    const messageElement = document.querySelector('#formMessage');
    if (!messageElement) return;

    messageElement.textContent = message;
    messageElement.className = `form-message ${type}`;
}

function isValidEmail(value) {
    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailPattern.test(value);
}

function escapeHtml(input) {
    return input
        .replaceAll('&', '&amp;')
        .replaceAll('<', '&lt;')
        .replaceAll('>', '&gt;')
        .replaceAll('"', '&quot;')
        .replaceAll("'", '&#39;');
}





