import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { autorService, livroService, mensagemDeErro } from '../services/api';
import FormCadastro from './FormCadastro';

function ler(livro) {
  return {
    titulo: livro.titulo ?? '',
    isbn: livro.isbn ?? '',
    anoPublicacao: livro.anoPublicacao ?? '',
    genero: livro.genero ?? '',
    autorId: livro.autor?.id != null ? String(livro.autor.id) : '',
  };
}

function preparar(livro) {
  return {
    titulo: livro.titulo.trim(),
    isbn: livro.isbn.trim() || null,
    anoPublicacao: livro.anoPublicacao === '' ? null : Number(livro.anoPublicacao),
    genero: livro.genero.trim() || null,
    autor: { id: Number(livro.autorId) }, // o JSON leva só o id do autor
  };
}

export default function LivroForm() {
  const { id } = useParams();
  const [autores, setAutores] = useState(null);
  const [erro, setErro] = useState('');

  useEffect(() => {
    let ativo = true;
    autorService.listar().then((lista) => {
      if (ativo) setAutores(lista);
    }).catch((falha) => {
      if (ativo) setErro(mensagemDeErro(falha, 'Não foi possível carregar os autores.'));
    });
    return () => { ativo = false; };
  }, []);

  if (erro) return <p role="alert" className="notice error">{erro}</p>;
  if (!autores) return <p role="status">Carregando…</p>;
  if (autores.length === 0) {
    return (
      <p className="notice error" role="alert">
        Cadastre um autor antes de cadastrar livros. <Link to="/autores/novo">Novo autor</Link>
      </p>
    );
  }

  const campos = [
    { nome: 'titulo', rotulo: 'Título', required: true, maxLength: 200, larguraTotal: true },
    { nome: 'autorId', rotulo: 'Autor', required: true, opcoes: autores },
    { nome: 'anoPublicacao', rotulo: 'Ano de publicação', type: 'number', min: 0, max: 9999 },
    { nome: 'genero', rotulo: 'Gênero', maxLength: 50 },
    { nome: 'isbn', rotulo: 'ISBN', maxLength: 20 },
  ];

  return (
    <FormCadastro
      key={id ?? 'novo'}
      singular="livro"
      plural="livros"
      rota="/livros"
      servico={livroService}
      campos={campos}
      ler={ler}
      preparar={preparar}
    />
  );
}
