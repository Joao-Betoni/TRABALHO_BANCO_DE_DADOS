    package Trabalho_BD.service;

    import Trabalho_BD.model.Usuario;
    import Trabalho_BD.dao.usuarioDao;
    import org.springframework.stereotype.Service;

    import java.util.Optional;
    import java.util.List;

    @Service
    public class usuarioService {

        private final usuarioDao usuarioDAO;

        public usuarioService(usuarioDao usuarioDAO) {
            this.usuarioDAO = usuarioDAO;
        }

        public void validarDadosCadastro(Usuario usuario) {

            if (usuario == null) {
                throw new IllegalArgumentException("Dados do usuário não informados.");
            }

            if (usuario.getNome() == null || usuario.getNome().isBlank()) {
                throw new IllegalArgumentException("O nome é obrigatório.");
            }

            if (usuario.getNomePerfil() == null || usuario.getNomePerfil().isBlank()) {
                throw new IllegalArgumentException("O nome de perfil é obrigatório.");
            }

            if (usuario.getEmail() == null || usuario.getEmail().isBlank()) {
                throw new IllegalArgumentException("O e-mail é obrigatório.");
            }

            if (usuario.getSenha() == null || usuario.getSenha().isBlank()) {
                throw new IllegalArgumentException("A senha é obrigatória.");
            }
        }

        public Optional<Usuario> buscarPorId(Long id) {

            if (id == null || id <= 0) {
                throw new IllegalArgumentException("O ID do usuário deve ser válido.");
            }

            return usuarioDAO.buscarPorId(id);
        }


        public Optional<Usuario> buscarPorEmail(String email) {

            if (email == null || email.isBlank()) {
                throw new IllegalArgumentException("O e-mail é obrigatório.");
            }

            return usuarioDAO.buscarPorEmail(email);
        }

        public Optional<Usuario> buscarPorNomePerfil(String nomePerfil) {

            if (nomePerfil == null || nomePerfil.isBlank()) {
                throw new IllegalArgumentException("O nome de perfil é obrigatório.");
            }

            return usuarioDAO.buscarPorNomePerfil(nomePerfil);
        }


        public Long cadastrarUsuario(Usuario usuario) {

            validarDadosCadastro(usuario);

            if (usuarioDAO.buscarPorEmail(usuario.getEmail()).isPresent()) {
                throw new IllegalArgumentException("O e-mail já está cadastrado.");
            }

            if (usuarioDAO.buscarPorNomePerfil(usuario.getNomePerfil()).isPresent()) {
                throw new IllegalArgumentException("O nome de perfil já está cadastrado.");
            }

            usuario.setAdmin(false);

            return usuarioDAO.inserirUsuario(usuario);
        }

        public boolean autenticarUsuario(String senha, String email) {

            if (senha == null || senha.isBlank() ||
                    email == null || email.isBlank()) {
                return false;
            }

            Optional<Usuario> usuario = buscarPorEmail(email);

            if (usuario.isEmpty()) {
                return false;
            }

            return usuario.get().getSenha().equals(senha);
        }

        public List<Usuario> listarUsuarios() {

            return usuarioDAO.listarUsuarios();
        }

        private int atualizarUsuario(Usuario usuario) {

            if (usuario == null || usuario.getId() == null || usuario.getId() <= 0) {
                throw new IllegalArgumentException("O ID do usuário deve ser válido.");
            }

            if (usuario.getNome() == null || usuario.getNome().isBlank()) {
                throw new IllegalArgumentException("O nome é obrigatório.");
            }

            if (usuario.getNomePerfil() == null || usuario.getNomePerfil().isBlank()) {
                throw new IllegalArgumentException("O nome de perfil é obrigatório.");
            }

            if (usuario.getEmail() == null || usuario.getEmail().isBlank()) {
                throw new IllegalArgumentException("O e-mail é obrigatório.");
            }

            if (usuarioDAO.buscarPorId(usuario.getId()).isEmpty()) {
                throw new IllegalArgumentException("Usuário não encontrado.");
            }

            Optional<Usuario> usuarioEmail = usuarioDAO.buscarPorEmail(usuario.getEmail());

            if (usuarioEmail.isPresent() &&
                    !usuarioEmail.get().getId().equals(usuario.getId())) {
                throw new IllegalArgumentException("O e-mail já pertence a outro usuário.");
            }

            Optional<Usuario> usuarioPerfil = usuarioDAO.buscarPorNomePerfil(usuario.getNomePerfil());

            if (usuarioPerfil.isPresent() &&
                    !usuarioPerfil.get().getId().equals(usuario.getId())) {
                throw new IllegalArgumentException("O nome de perfil já pertence a outro usuário.");
            }

            return usuarioDAO.atualizarUsuario(usuario);
        }

        public boolean excluirUsuarioPorID(Long id) {

            Optional<Usuario> usuario = buscarPorId(id);

            if (usuario.isEmpty()) {
                return false;
            }

            int linhasAfetadas = usuarioDAO.excluirUsuario(id);

            return linhasAfetadas > 0;
        }

    }
