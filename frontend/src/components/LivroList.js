import { livroService } from '../services/api';
import ListaCadastro from './ListaCadastro';

const colunas = [
  { rotulo: 'Título', valor: (livro) => livro.titulo },
  { rotulo: 'Autor', valor: (livro) => livro.autor?.nome },
  { rotulo: 'Ano', valor: (livro) => livro.anoPublicacao },
  { rotulo: 'Gênero', valor: (livro) => livro.genero },
  { rotulo: 'ISBN', valor: (livro) => livro.isbn },
];

export default function LivroList() {
  return (
    <ListaCadastro
      titulo="Livros"
      singular="livro"
      rota="/livros"
      servico={livroService}
      colunas={colunas}
    />
  );
}
