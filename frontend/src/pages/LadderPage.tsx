import { useEffect, useState, useCallback } from 'react';
import Header from '../components/Header';
import LadderTable from '../features/ladder/LadderTable';
import { fetchLadder, fetchLiveLadder } from '../api/client';
import type { Ladder } from '../types/api';

function sortLadder(entries: Ladder[]): Ladder[] {
  return [...entries].sort((a, b) => {
    if (b.pts !== a.pts) return b.pts - a.pts;
    if (b.percentage !== a.percentage) return b.percentage - a.percentage;
    return b.pointsFor - a.pointsFor;
  });
}

export default function LadderPage() {
  const [ladder, setLadder] = useState<Ladder[]>([]);
  const [liveLadder, setLiveLadder] = useState<Ladder[]>([]);
  const [error, setError] = useState<string | null>(null);

  const load = useCallback(async () => {
    try {
      const [l, live] = await Promise.all([fetchLadder(), fetchLiveLadder()]);
      setLadder(sortLadder(l));
      setLiveLadder(sortLadder(live));
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Failed to load ladder');
    }
  }, []);

  useEffect(() => {
    load();
    const interval = setInterval(load, 5 * 60 * 1000);
    return () => clearInterval(interval);
  }, [load]);

  const currentRound = ladder[0]?.round ?? null;

  return (
    <div className="min-h-screen bg-gray-50">
      <Header />
      <main className="max-w-3xl mx-auto px-4 py-6">
        {error && (
          <div className="mb-4 p-3 bg-red-100 text-red-700 rounded border border-red-200">{error}</div>
        )}
        {liveLadder.length > 0 && (
          <LadderTable entries={liveLadder} title="Live Ladder" showAverages={false} />
        )}
        {ladder.length > 0 && (
          <LadderTable
            entries={ladder}
            title={currentRound != null ? `Round ${currentRound} Ladder` : 'Ladder'}
            showAverages={true}
          />
        )}
      </main>
    </div>
  );
}
