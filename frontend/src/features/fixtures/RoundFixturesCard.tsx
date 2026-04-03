import { useNavigate } from 'react-router-dom';
import type { RoundFixtures } from '../../types/api';

interface Props {
  round: RoundFixtures;
}

export default function RoundFixturesCard({ round }: Props) {
  const navigate = useNavigate();

  return (
    <div className="rounded border border-gray-200 shadow-sm overflow-hidden">
      <div className="bg-gray-100 px-4 py-2 font-semibold border-b border-gray-200">
        Round {round.round}
      </div>
      <div className="overflow-x-auto">
        <table className="w-full text-sm">
          <thead className="bg-gray-50 text-gray-600 uppercase text-xs">
            <tr>
              <th className="px-3 py-2 text-left">Home</th>
              <th className="px-3 py-2 text-right">Score</th>
              <th className="px-3 py-2 text-center">vs</th>
              <th className="px-3 py-2 text-left">Score</th>
              <th className="px-3 py-2 text-right">Away</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-100">
            {round.games.map(game => (
              <tr
                key={game.game}
                onClick={() => game.resultsUri && navigate(game.resultsUri)}
                className={`${game.resultsUri ? 'cursor-pointer hover:bg-blue-50' : ''} bg-white`}
              >
                <td className="px-3 py-2">{game.homeTeamDisplayName}</td>
                <td className="px-3 py-2 text-right">{game.homeTeamScore}</td>
                <td className="px-3 py-2 text-center text-gray-400">vs</td>
                <td className="px-3 py-2">{game.awayTeamScore}</td>
                <td className="px-3 py-2 text-right">{game.awayTeamDisplayName}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
