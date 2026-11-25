// tela cadastre-se.html: POST /uel/usuarios (RF2)
import { apiJson, mostrarAlerta } from './app.js';
const form = document.querySelector('#form-cadastro');
form.addEventListener('submit', async (evento) => {
    evento.preventDefault();
    try {
        const corpo = {
            nome: document.getElementById('nome').value,
            nomePerfil: document.getElementById('nomePerfil').value,
            email: document.getElementById('email').value,
            senha: document.getElementById('senha').value,
        };
        await apiJson('/usuarios', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(corpo),
        });
        window.location.href = 'index.html'; // conta criada: volta pro login
    }
    catch (erro) {
        mostrarAlerta('alerta', erro.message); // email/perfil duplicado (409)
    }
});
