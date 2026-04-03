import { useEffect, useState, useCallback } from 'react';
import Header from '../components/Header';
import RoundFixturesCard from '../features/fixtures/RoundFixturesCard';
import { fetchFixtures } from '../api/client';
import type { RoundFixtures } from '../types/api';

export default function FixturesPage() {
  const [fixtures, setFixtures] = useState<RoundFixtures[]>([]);
  const [error, setError] = useState<string | null>(null);

  const load = useCallback(async () => {
    try {
      setFixtures(await fetchFixtures());
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Failed to load fixtures');
    }
  }, []);

  useEffect(() => { load(); }, [load]);

  const rows: RoundFixtures[][] = [];
  for (let i = 0; i < fixtures.length; i += 3) {
    rows.push(fixtures.slice(i, i + 3));
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <Header />
      <main className="max-w-screen-xl mx-auto px-4 py-6">
        {error && (
          <div className="mb-4 p-3 bg-red-100 text-red-700 rounded border border-red-200">{error}</div>
        )}
        {rows.map((row, ri) => (
          <div key={ri} className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-4">
            {row.map(round => (
              <RoundFixturesCard key={round.round} round={round} />
            ))}
          </div>
        ))}
      </main>
    </div>
  );
}
