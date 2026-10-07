const promptInput = document.querySelector('#prompt');
const count = document.querySelector('#character-count');
const compareButton = document.querySelector('#compare-button');
const chatButton = document.querySelector('#chat-button');
const compareResults = document.querySelector('#comparison-results');
const singleResult = document.querySelector('#single-result');
const summary = document.querySelector('#compare-summary');
const apiSelection = document.querySelector('#api-selection');
const comparePage = document.querySelector('#compare-page');
const chatPage = document.querySelector('#chat-page');

apiSelection.addEventListener('change', () => {
    const compareSelected = apiSelection.value === 'compare';
    comparePage.classList.toggle('hidden-page', !compareSelected);
    chatPage.classList.toggle('hidden-page', compareSelected);
});

function updateCount() { count.textContent = `${promptInput.value.length.toLocaleString()} / 10,000`; }
promptInput.addEventListener('input', updateCount); updateCount();
document.querySelector('#clear-prompt').addEventListener('click', () => { promptInput.value = ''; updateCount(); promptInput.focus(); });

function resultCard(result, key) {
    const success = result.status === 'SUCCESS';
    const title = `${pretty(result.integration)} · ${pretty(result.provider)}`;
    return `<article class="result-card"><div class="card-top"><div><div class="route-name">${escapeHtml(title)}</div><div class="route-meta">${escapeHtml(result.model || key || 'Configured model')}</div></div><span class="badge ${success ? 'success' : 'failed'}">${success ? 'Success' : 'Failed'}</span></div><div class="response">${escapeHtml(success ? result.message : result.error || 'The route failed without an error message.')}</div><div class="card-footer">${Number(result.durationMs || 0).toLocaleString()} ms</div></article>`;
}
function pretty(value) { return String(value || '').replaceAll('-', ' ').replace(/\b\w/g, c => c.toUpperCase()).replace('Sdk', 'SDK'); }
function escapeHtml(value) { return String(value ?? '').replace(/[&<>'"]/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;',"'":'&#39;','"':'&quot;'}[c])); }
function setBusy(button, busy, text) { button.disabled = busy; if (busy) button.dataset.label = button.innerHTML; button.innerHTML = busy ? 'Running…' : (button.dataset.label || text); }
function promptOrStop() { if (!promptInput.value.trim()) { promptInput.focus(); return false; } return true; }

compareButton.addEventListener('click', async () => {
    if (!promptOrStop()) return;
    setBusy(compareButton, true, 'Run comparison'); summary.classList.add('hidden');
    try {
        const response = await fetch('/api/v1/ai/compare', { method:'POST', headers:{'Content-Type':'application/json'}, body:JSON.stringify({prompt:promptInput.value}) });
        const data = await response.json(); if (!response.ok) throw new Error(data.message || 'Comparison request failed.');
        const entries = Object.entries(data); compareResults.className = 'results-grid'; compareResults.innerHTML = entries.map(([key, value]) => resultCard(value, key)).join('');
        summary.textContent = `${entries.length} routes completed`; summary.classList.remove('hidden');
    } catch (error) { compareResults.className = 'results-grid empty-state'; compareResults.innerHTML = `<div class="empty-icon">!</div><p>${escapeHtml(error.message)}</p>`; }
    finally { setBusy(compareButton, false, 'Run comparison'); }
});

chatButton.addEventListener('click', async () => {
    if (!promptOrStop()) return;
    setBusy(chatButton, true, 'Run selected route');
    try {
        const response = await fetch('/api/v1/ai/chat', { method:'POST', headers:{'Content-Type':'application/json'}, body:JSON.stringify({prompt:promptInput.value, integration:document.querySelector('#integration').value, provider:document.querySelector('#provider').value}) });
        const data = await response.json(); if (!response.ok) throw new Error(data.message || 'Chat request failed.');
        singleResult.className = 'single-result has-result'; singleResult.innerHTML = resultCard(data, 'selected route');
    } catch (error) { singleResult.className = 'single-result empty-state'; singleResult.innerHTML = `<div class="empty-icon">!</div><p>${escapeHtml(error.message)}</p>`; }
    finally { setBusy(chatButton, false, 'Run selected route'); }
});
