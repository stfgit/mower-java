// Variables globales
let currentLawnWidth = 5;
let currentLawnHeight = 5;
let currentMower = null;
let mowerPath = [];

// Configuration API
const API_BASE = '/api/mower';

// Initialisation
document.addEventListener('DOMContentLoaded', function() {
    createLawn();
    setStatus('Interface prête');
});

// Gestion du statut
function setStatus(message, type = 'info') {
    const statusElement = document.getElementById('status');
    statusElement.textContent = message;
    statusElement.className = `status ${type}`;
}

// Création de la pelouse
function createLawn() {
    const width = parseInt(document.getElementById('lawnWidth').value);
    const height = parseInt(document.getElementById('lawnHeight').value);
    
    if (width < 1 || width > 20 || height < 1 || height > 20) {
        setStatus('Dimensions invalides (1-20)', 'error');
        return;
    }
    
    currentLawnWidth = width;
    currentLawnHeight = height;
    
    const lawnGrid = document.getElementById('lawnGrid');
    lawnGrid.style.gridTemplateColumns = `repeat(${width + 1}, 1fr)`;
    lawnGrid.innerHTML = '';
    
    // Création de la grille (coordonnées inversées pour affichage)
    for (let y = height; y >= 0; y--) {
        for (let x = 0; x <= width; x++) {
            const cell = document.createElement('div');
            cell.className = 'lawn-cell';
            cell.dataset.x = x;
            cell.dataset.y = y;
            
            // Ajout des coordonnées
            const coords = document.createElement('div');
            coords.className = 'coordinates';
            coords.textContent = `${x},${y}`;
            cell.appendChild(coords);
            
            // Événement de clic pour placer la tondeuse
            cell.addEventListener('click', () => placeMowerAt(x, y));
            
            lawnGrid.appendChild(cell);
        }
    }
    
    // Mise à jour des limites des inputs
    document.getElementById('mowerX').max = width;
    document.getElementById('mowerY').max = height;
    
    setStatus(`Pelouse créée (${width}x${height})`, 'success');
    clearMower();
}

// Placement de la tondeuse à une position donnée
function placeMowerAt(x, y) {
    document.getElementById('mowerX').value = x;
    document.getElementById('mowerY').value = y;
    placeMower();
}

// Placement de la tondeuse
function placeMower() {
    const x = parseInt(document.getElementById('mowerX').value);
    const y = parseInt(document.getElementById('mowerY').value);
    const direction = document.getElementById('mowerDirection').value;
    
    if (x < 0 || x > currentLawnWidth || y < 0 || y > currentLawnHeight) {
        setStatus('Position hors limites', 'error');
        return;
    }
    
    clearMower();
    
    currentMower = { x, y, direction };
    mowerPath = [{ x, y }];
    
    updateMowerDisplay();
    setStatus(`Tondeuse placée en (${x}, ${y}) direction ${direction}`, 'success');
}

// Mise à jour de l'affichage de la tondeuse
function updateMowerDisplay() {
    // Effacer l'ancienne position
    document.querySelectorAll('.lawn-cell').forEach(cell => {
        cell.classList.remove('mower', 'path');
        cell.textContent = '';
        // Garder les coordonnées
        if (!cell.querySelector('.coordinates')) {
            const coords = document.createElement('div');
            coords.className = 'coordinates';
            coords.textContent = `${cell.dataset.x},${cell.dataset.y}`;
            cell.appendChild(coords);
        }
    });
    
    if (!currentMower) return;
    
    // Marquer le chemin
    mowerPath.forEach(pos => {
        const cell = findCell(pos.x, pos.y);
        if (cell && !cell.classList.contains('mower')) {
            cell.classList.add('path');
        }
    });
    
    // Placer la tondeuse
    const mowerCell = findCell(currentMower.x, currentMower.y);
    if (mowerCell) {
        mowerCell.classList.add('mower');
        mowerCell.textContent = getDirectionSymbol(currentMower.direction);
    }
}

// Trouver une cellule par coordonnées
function findCell(x, y) {
    return document.querySelector(`[data-x="${x}"][data-y="${y}"]`);
}

// Obtenir le symbole de direction
function getDirectionSymbol(direction) {
    const symbols = { 'N': '↑', 'E': '→', 'S': '↓', 'W': '←' };
    return symbols[direction] || '?';
}

// Effacer la tondeuse
function clearMower() {
    currentMower = null;
    mowerPath = [];
    document.querySelectorAll('.lawn-cell').forEach(cell => {
        cell.classList.remove('mower', 'path');
        cell.textContent = '';
        // Restaurer les coordonnées
        if (!cell.querySelector('.coordinates')) {
            const coords = document.createElement('div');
            coords.className = 'coordinates';
            coords.textContent = `${cell.dataset.x},${cell.dataset.y}`;
            cell.appendChild(coords);
        }
    });
}

// Envoi d'une commande unique
async function sendCommand(command) {
    if (!currentMower) {
        setStatus('Placez d\'abord la tondeuse', 'error');
        return;
    }
    
    try {
        setStatus('Envoi de la commande...', 'info');
        
        const response = await fetch(`${API_BASE}/execute`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                lawnDimensions: `${currentLawnWidth} ${currentLawnHeight}`,
                mowerPosition: `${currentMower.x} ${currentMower.y} ${currentMower.direction}`,
                commands: command
            })
        });
        
        const result = await response.json();
        
        if (response.ok) {
            // Mettre à jour la position de la tondeuse
            currentMower.x = result.x;
            currentMower.y = result.y;
            currentMower.direction = result.direction;
            
            // Ajouter au chemin si position différente
            const lastPos = mowerPath[mowerPath.length - 1];
            if (!lastPos || lastPos.x !== result.x || lastPos.y !== result.y) {
                mowerPath.push({ x: result.x, y: result.y });
            }
            
            updateMowerDisplay();
            addResult(`Commande ${command}: ${result.position}`, 'success');
            setStatus(`Position: ${result.position}`, 'success');
        } else {
            throw new Error('Erreur lors de l\'exécution');
        }
    } catch (error) {
        setStatus('Erreur de communication', 'error');
        addResult(`Erreur: ${error.message}`, 'error');
    }
}

// Exécution d'une séquence
async function executeSequence() {
    const sequence = document.getElementById('commandSequence').value.toUpperCase();
    
    if (!sequence) {
        setStatus('Saisissez une séquence de commandes', 'error');
        return;
    }
    
    if (!currentMower) {
        setStatus('Placez d\'abord la tondeuse', 'error');
        return;
    }
    
    try {
        setStatus('Exécution de la séquence...', 'info');
        
        const response = await fetch(`${API_BASE}/execute`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                lawnDimensions: `${currentLawnWidth} ${currentLawnHeight}`,
                mowerPosition: `${currentMower.x} ${currentMower.y} ${currentMower.direction}`,
                commands: sequence
            })
        });
        
        const result = await response.json();
        
        if (response.ok) {
            // Mettre à jour la position finale
            currentMower.x = result.x;
            currentMower.y = result.y;
            currentMower.direction = result.direction;
            
            // Simuler le chemin (approximation)
            mowerPath.push({ x: result.x, y: result.y });
            
            updateMowerDisplay();
            addResult(`Séquence "${sequence}": ${result.position}`, 'success');
            setStatus(`Séquence terminée: ${result.position}`, 'success');
            
            // Effacer le champ
            document.getElementById('commandSequence').value = '';
        } else {
            throw new Error('Erreur lors de l\'exécution');
        }
    } catch (error) {
        setStatus('Erreur de communication', 'error');
        addResult(`Erreur: ${error.message}`, 'error');
    }
}

// Exécution en mode batch
async function executeBatch() {
    const batchText = document.getElementById('batchCommands').value.trim();
    
    if (!batchText) {
        setStatus('Saisissez les commandes batch', 'error');
        return;
    }
    
    try {
        const lines = batchText.split('\n').map(line => line.trim()).filter(line => line);
        const mowers = [];
        
        for (let i = 0; i < lines.length; i += 2) {
            if (i + 1 < lines.length) {
                mowers.push({
                    mowerPosition: lines[i],
                    commands: lines[i + 1]
                });
            }
        }
        
        if (mowers.length === 0) {
            setStatus('Format batch invalide', 'error');
            return;
        }
        
        setStatus('Exécution du batch...', 'info');
        
        const response = await fetch(`${API_BASE}/batch`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                lawnDimensions: `${currentLawnWidth} ${currentLawnHeight}`,
                mowers: mowers
            })
        });
        
        const results = await response.json();
        
        if (response.ok && Array.isArray(results)) {
            clearMower();
            results.forEach((result, index) => {
                addResult(`Tondeuse ${index + 1}: ${result.position}`, 'success');
            });
            setStatus(`Batch terminé: ${results.length} tondeuses`, 'success');
            
            // Effacer le champ
            document.getElementById('batchCommands').value = '';
        } else {
            throw new Error('Erreur lors de l\'exécution batch');
        }
    } catch (error) {
        setStatus('Erreur de communication', 'error');
        addResult(`Erreur batch: ${error.message}`, 'error');
    }
}

// Ajout d'un résultat
function addResult(message, type = 'info') {
    const resultsDiv = document.getElementById('results');
    const resultItem = document.createElement('div');
    resultItem.className = `result-item ${type}`;
    
    const timestamp = new Date().toLocaleTimeString();
    resultItem.textContent = `[${timestamp}] ${message}`;
    
    resultsDiv.insertBefore(resultItem, resultsDiv.firstChild);
    
    // Limiter à 20 résultats
    while (resultsDiv.children.length > 20) {
        resultsDiv.removeChild(resultsDiv.lastChild);
    }
}