import { useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { mensagemDeErro } from '../services/api';

export default function FormCadastro({ singular, plural, rota, servico, campos, ler, preparar }) {
  const { id } = useParams();
  const navegar = useNavigate();
  const [dados, setDados] = useState(() => id ? null : ler({}));
  const [erro, setErro] = useState('');
  const [salvando, setSalvando] = useState(false);

  useEffect(() => {
    if (!id) return undefined;

    let ativo = true;
    servico.buscar(id).then((registro) => {
      if (ativo) setDados(ler(registro));
    }).catch((falha) => {
      if (ativo) setErro(mensagemDeErro(falha, 'Não foi possível carregar este registro.'));
    });
    return () => { ativo = false; };
  }, [id, ler, servico]);

  async function salvar(evento) {
    evento.preventDefault();
    setSalvando(true);
    setErro('');
    try {
      const payload = preparar(dados);
      if (id) {
        await servico.atualizar(id, payload);
      } else {
        await servico.criar(payload);
      }
      navegar(rota);
    } catch (falha) {
      setErro(mensagemDeErro(falha, 'Não foi possível salvar. Confira os campos e tente novamente.'));
    } finally {
      setSalvando(false);
    }
  }

  return (
    <>
      <Link className="back-link" to={rota}>Voltar para {plural}</Link>
      <h1 className="form-title">{id ? 'Editar' : 'Novo'} {singular}</h1>
      {erro && <p role="alert" className="notice error">{erro}</p>}
      {!dados && !erro && <p role="status">Carregando…</p>}
      {dados && (
        <form className="panel form-panel" onSubmit={salvar}>
          <fieldset disabled={salvando}>
            <legend className="sr-only">Dados do {singular}</legend>
            <div className="form-fields">
              {campos.map(({ nome, rotulo, opcoes, larguraTotal, ...atributos }) => {
                const propriedades = {
                  id: nome,
                  name: nome,
                  value: dados[nome],
                  onChange: (evento) => {
                    const valor = evento.target.value;
                    setDados((atuais) => ({ ...atuais, [nome]: valor }));
                  },
                  ...atributos,
                };
                return (
                  <div key={nome} className={larguraTotal ? 'field full-width' : 'field'}>
                    <label htmlFor={nome}>
                      {rotulo}{atributos.required && <span aria-hidden="true"> *</span>}
                    </label>
                    {opcoes ? (
                      <select {...propriedades}>
                        <option value="">Selecione um autor</option>
                        {opcoes.map(({ id: opcaoId, nome: opcaoNome }) => (
                          <option key={opcaoId} value={opcaoId}>{opcaoNome}</option>
                        ))}
                      </select>
                    ) : <input {...propriedades} />}
                  </div>
                );
              })}
            </div>
            <div className="form-actions">
              <button className="button primary" type="submit">
                {salvando ? 'Salvando…' : 'Salvar'}
              </button>
              <Link className="button secondary" to={rota}>Cancelar</Link>
            </div>
          </fieldset>
        </form>
      )}
    </>
  );
}
