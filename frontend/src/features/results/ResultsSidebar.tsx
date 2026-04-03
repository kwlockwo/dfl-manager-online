import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import type { RoundMenu } from '../../types/api';

interface Props {
  menu: RoundMenu[];
}

export default function ResultsSidebar({ menu }: Props) {
  const [openRounds, setOpenRounds] = useState<Set<number>>(new Set());

  useEffect(() => {
    const active = new Set(
      menu.filter(r => r.active).map(r => r.round)
    );
    setOpenRounds(active);
  }, [menu]);

  const toggleRound = (round: number) => {
    setOpenRounds(prev => {
      const next = new Set(prev);
      if (next.has(round)) {
        next.delete(round);
      } else {
        next.add(round);
      }
      return next;
    });
  };

  return (
    <div className="bg-white rounded border border-gray-200 shadow-sm overflow-hidden">
      <div className="bg-gray-100 px-4 py-2 font-semibold border-b border-gray-200 text-sm">
        Fixtures
      </div>
      <nav className="p-2">
        {menu.map(round => (
          <div key={round.round} className="mb-1">
            <button
              onClick={() => toggleRound(round.round)}
              className="w-full text-left px-3 py-1.5 text-sm font-medium text-gray-700 hover:bg-gray-100 rounded flex justify-between items-center"
            >
              <span>Round {round.round}</span>
              <span className="text-gray-400 text-xs">{openRounds.has(round.round) ? '▲' : '▼'}</span>
            </button>
            {openRounds.has(round.round) && (
              <ul className="ml-3 mt-0.5 space-y-0.5">
                {round.games.map(game => (
                  <li key={game.game}>
                    <Link
                      to={game.resultsUri}
                      className={`block px-3 py-1 text-xs rounded hover:bg-blue-50 hover:text-blue-700 ${
                        game.active ? 'font-bold text-blue-700 bg-blue-50' : 'text-gray-600'
                      }`}
                    >
                      {game.homeTeam} v {game.awayTeam}
                    </Link>
                  </li>
                ))}
              </ul>
            )}
          </div>
        ))}
      </nav>
    </div>
  );
}
