// tela index.html: login simples por sessão (RF2)
import { apiJson, mostrarAlerta } from './app.js';
const form = document.querySelector('#form-login');
form.addEventListener('submit', async (evento) => {
    evento.preventDefault();
    const email = document.getElementById('email').value;
    const senha = document.getElementById('senha').value;
    try {
        await apiJson('/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, senha }),
        });
        window.location.href = 'catalogo.html';
    }
    catch (erro) {
        mostrarAlerta('alerta', erro.message);
    }
});
