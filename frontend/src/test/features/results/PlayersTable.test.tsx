import { render, screen } from '@testing-library/react';
import PlayersTable from '../../../features/results/PlayersTable';
import type { SelectedPlayer, TeamResults } from '../../../types/api';

const makePlayer = (
  id: number,
  name: string,
  position: string,
  score: number,
  scrapingStatus: string | null,
  replacementInd: string | null = null
): SelectedPlayer => ({
  playerId: id,
  teamPlayerId: id,
  name,
  position,
  hasPlayer: true,
  scoreUsed: true,
  dnp: false,
  replacementInd,
  emgSort: 0,
  stats: {
    kicks: 10, handballs: 5, disposals: 15, marks: 4,
    hitouts: 0, freesFor: 1, freesAgainst: 1, tackles: 3,
    goals: 1, behinds: 0, score, predictedScore: 60, trend: score - 60,
    scrapingStatus,
  },
});

const makeTeam = (players: SelectedPlayer[], score: number, currentPredictedScore: number, predictedScore: number): TeamResults => ({
  teamCode: 'AAA',
  teamName: 'Alpha',
  players,
  emergencies: [],
  score,
  currentPredictedScore,
  predictedScore,
  trend: score - predictedScore,
  emgInd: null,
});

describe('PlayersTable', () => {
  it('renders player names', () => {
    const players = [makePlayer(1, 'John Smith', 'FWD', 80, null)];
    render(<PlayersTable players={players} team={makeTeam(players, 80, 80, 80)} showStats={false} />);
    expect(screen.getByText('John Smith')).toBeInTheDocument();
  });

  it('appends * suffix for players with replacementInd *', () => {
    const players = [makePlayer(1, 'John Smith', 'FWD', 80, null, '*')];
    render(<PlayersTable players={players} team={makeTeam(players, 80, 80, 80)} showStats={false} />);
    expect(screen.getByText('John Smith*')).toBeInTheDocument();
  });

  it('appends ** suffix for players with replacementInd **', () => {
    const players = [makePlayer(1, 'Jane Doe', 'MID', 70, null, '**')];
    render(<PlayersTable players={players} team={makeTeam(players, 70, 70, 70)} showStats={false} />);
    expect(screen.getByText('Jane Doe**')).toBeInTheDocument();
  });

  it('applies yellow background for InProgress status', () => {
    const players = [makePlayer(1, 'John Smith', 'FWD', 50, 'InProgress')];
    render(<PlayersTable players={players} team={makeTeam(players, 50, 50, 50)} showStats={false} />);
    const rows = screen.getAllByRole('row');
    const dataRow = rows.find(r => r.textContent?.includes('John Smith'));
    expect(dataRow?.className).toContain('bg-yellow-50');
  });

  it('applies blue background for Completed status', () => {
    const players = [makePlayer(1, 'John Smith', 'FWD', 80, 'Completed')];
    render(<PlayersTable players={players} team={makeTeam(players, 80, 80, 80)} showStats={false} />);
    const rows = screen.getAllByRole('row');
    const dataRow = rows.find(r => r.textContent?.includes('John Smith'));
    expect(dataRow?.className).toContain('bg-blue-100');
  });

  it('applies green background for Finalized status', () => {
    const players = [makePlayer(1, 'John Smith', 'FWD', 80, 'Finalized')];
    render(<PlayersTable players={players} team={makeTeam(players, 80, 80, 80)} showStats={false} />);
    const rows = screen.getAllByRole('row');
    const dataRow = rows.find(r => r.textContent?.includes('John Smith'));
    expect(dataRow?.className).toContain('bg-green-100');
  });

  it('renders single footer row when currentPredictedScore equals score', () => {
    const players = [makePlayer(1, 'John Smith', 'FWD', 80, null)];
    render(<PlayersTable players={players} team={makeTeam(players, 80, 80, 75)} showStats={false} />);
    expect(screen.queryByText('Pre-game')).not.toBeInTheDocument();
    expect(screen.getByText('Total')).toBeInTheDocument();
  });

  it('renders pre-game footer row when currentPredictedScore differs from score', () => {
    const players = [makePlayer(1, 'John Smith', 'FWD', 80, null)];
    render(<PlayersTable players={players} team={makeTeam(players, 80, 95, 75)} showStats={false} />);
    expect(screen.getByText('Pre-game')).toBeInTheDocument();
    expect(screen.getByText('Total')).toBeInTheDocument();
  });

  it('sorts players by position asc then score desc', () => {
    const players = [
      makePlayer(1, 'Alpha', 'MID', 90, null),
      makePlayer(2, 'Beta', 'FWD', 100, null),
      makePlayer(3, 'Gamma', 'FWD', 80, null),
    ];
    render(<PlayersTable players={players} team={makeTeam(players, 270, 270, 270)} showStats={false} />);
    const rows = screen.getAllByRole('row').slice(2); // skip two thead rows
    expect(rows[0].textContent).toContain('Beta');   // FWD, score 100
    expect(rows[1].textContent).toContain('Gamma');  // FWD, score 80
    expect(rows[2].textContent).toContain('Alpha');  // MID
  });
});
