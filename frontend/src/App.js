import { BrowserRouter, Navigate, NavLink, Route, Routes } from 'react-router-dom';
import AutorList from './components/AutorList';
import AutorForm from './components/AutorForm';
import LivroList from './components/LivroList';
import LivroForm from './components/LivroForm';
import './App.css';

export default function App() {
  return (
    <BrowserRouter>
      <header className="topbar">
        <span className="brand">Biblioteca Web</span>
        <nav aria-label="Principal">
          <NavLink to="/autores">Autores</NavLink>
          <NavLink to="/livros">Livros</NavLink>
        </nav>
      </header>
      <main className="page">
        <Routes>
          <Route path="/" element={<Navigate to="/livros" replace />} />
          <Route path="/autores" element={<AutorList />} />
          <Route path="/autores/novo" element={<AutorForm />} />
          <Route path="/autores/editar/:id" element={<AutorForm />} />
          <Route path="/livros" element={<LivroList />} />
          <Route path="/livros/novo" element={<LivroForm />} />
          <Route path="/livros/editar/:id" element={<LivroForm />} />
        </Routes>
      </main>
    </BrowserRouter>
  );
}
