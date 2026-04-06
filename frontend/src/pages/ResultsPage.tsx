import { useEffect, useState, useCallback } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import Header from '../components/Header';
import ResultsSidebar from '../features/results/ResultsSidebar';
import TeamResultsPanel from '../features/results/TeamResultsPanel';
import { fetchResults, fetchCurrentResults, fetchResultsMenu } from '../api/client';
import type { Results, RoundMenu } from '../types/api';

export default function ResultsPage() {
  const { round: roundParam, game: gameParam } = useParams<{ round: string; game: string }>();
  const navigate = useNavigate();

  const [results, setResults] = useState<Results | null>(null);
  const [menu, setMenu] = useState<RoundMenu[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const [showStats, setShowStats] = useState(false);

  const load = useCallback(async () => {
    try {
      let round: number;
      let game: number;

      if (roundParam != null && gameParam != null) {
        round = parseInt(roundParam, 10);
        game = parseInt(gameParam, 10);
      } else {
        const current = await fetchCurrentResults();
        navigate(`/results/${current.round}/${current.game}`, { replace: true });
        return;
      }

      const [r, m] = await Promise.all([
        fetchResults(round, game),
        fetchResultsMenu(round, game),
      ]);
      setResults(r);
      setMenu(m);
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Failed to load results');
    }
  }, [roundParam, gameParam, navigate]);

  useEffect(() => {
    load();
    const interval = setInterval(load, 5 * 60 * 1000);
    return () => clearInterval(interval);
  }, [load]);

  return (
    <div className="min-h-screen bg-gray-50 flex flex-col">
      <Header />
      <div className="max-w-screen-xl mx-auto w-full px-4 py-6">
        {/* Mobile controls */}
        <div className="md:hidden mb-3 flex gap-2">
          <button
            onClick={() => setSidebarOpen(o => !o)}
            className="text-sm px-3 py-1.5 rounded border border-gray-300 bg-white hover:bg-gray-50"
          >
            {sidebarOpen ? 'Hide fixtures' : 'Show fixtures'}
          </button>
          <button
            onClick={() => setShowStats(s => !s)}
            className="text-sm px-3 py-1.5 rounded border border-gray-300 bg-white hover:bg-gray-50"
          >
            {showStats ? 'Hide stats' : 'Show stats'}
          </button>
        </div>
        {sidebarOpen && (
          <div className="md:hidden mb-4">
            <ResultsSidebar menu={menu} />
          </div>
        )}

        <div className="flex flex-1 gap-4">
          {/* Desktop sidebar */}
          <aside className="hidden md:block w-52 shrink-0">
            <ResultsSidebar menu={menu} />
          </aside>
          <main className="flex-1 min-w-0">
            {error && (
              <div className="mb-4 p-3 bg-red-100 text-red-700 rounded border border-red-200">{error}</div>
            )}
            {results && results.homeTeam && results.awayTeam ? (
              <div className="space-y-6">
                <TeamResultsPanel team={results.homeTeam} label="Home" showStats={showStats} />
                <TeamResultsPanel team={results.awayTeam} label="Away" showStats={showStats} />
              </div>
            ) : results ? (
              <div className="text-gray-500 italic">No results available for this game yet.</div>
            ) : null}
          </main>
        </div>
      </div>
    </div>
  );
}
