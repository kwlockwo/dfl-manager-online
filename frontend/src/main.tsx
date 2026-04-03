import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import './index.css';
import ErrorBoundary from './components/ErrorBoundary';
import LadderPage from './pages/LadderPage';
import FixturesPage from './pages/FixturesPage';
import ResultsPage from './pages/ResultsPage';

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <ErrorBoundary>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<LadderPage />} />
          <Route path="/fixtures" element={<FixturesPage />} />
          <Route path="/results" element={<ResultsPage />} />
          <Route path="/results/:round/:game" element={<ResultsPage />} />
        </Routes>
      </BrowserRouter>
    </ErrorBoundary>
  </StrictMode>,
);
