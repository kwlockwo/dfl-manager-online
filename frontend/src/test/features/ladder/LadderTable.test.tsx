import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import LadderTable from '../../../features/ladder/LadderTable';
import type { Ladder } from '../../../types/api';

const makeEntry = (teamCode: string, displayName: string, pts: number, percentage: number, pointsFor: number): Ladder => ({
  teamCode,
  displayName,
  pts,
  percentage,
  pointsFor,
  wins: 3,
  losses: 1,
  draws: 0,
  pointsAgainst: 300,
  averageFor: 90.5,
  averageAgainst: 75,
  round: 4,
  teamUri: `/teams/${teamCode}`,
});

const entries: Ladder[] = [
  makeEntry('AAA', 'Alpha', 8, 110.5, 500),
  makeEntry('BBB', 'Beta', 12, 125, 600),
  makeEntry('CCC', 'Gamma', 12, 130, 550),
];

describe('LadderTable', () => {
  it('renders the title', () => {
    render(<LadderTable entries={entries} title="Round 4 Ladder" showAverages={true} />);
    expect(screen.getByText('Round 4 Ladder')).toBeInTheDocument();
  });

  it('renders all team names', () => {
    render(<LadderTable entries={entries} title="Ladder" showAverages={true} />);
    expect(screen.getByText('Alpha')).toBeInTheDocument();
    expect(screen.getByText('Beta')).toBeInTheDocument();
    expect(screen.getByText('Gamma')).toBeInTheDocument();
  });

  it('shows average columns when showAverages is true', () => {
    render(<LadderTable entries={entries} title="Ladder" showAverages={true} />);
    expect(screen.getByText('Ave For')).toBeInTheDocument();
    expect(screen.getByText('Ave Agst')).toBeInTheDocument();
    expect(screen.getByText('Agst')).toBeInTheDocument();
  });

  it('hides average columns when showAverages is false', () => {
    render(<LadderTable entries={entries} title="Live Ladder" showAverages={false} />);
    expect(screen.queryByText('Ave For')).not.toBeInTheDocument();
    expect(screen.queryByText('Ave Agst')).not.toBeInTheDocument();
  });

  it('formats percentage to 2 decimal places', () => {
    render(<LadderTable entries={entries} title="Ladder" showAverages={true} />);
    expect(screen.getByText('110.50')).toBeInTheDocument();
  });

  it('toggles sort direction when clicking the same column header twice', async () => {
    const user = userEvent.setup();
    render(<LadderTable entries={entries} title="Ladder" showAverages={true} />);

    const ptsHeader = screen.getByText(/^Pts/);
    const rowsBefore = screen.getAllByRole('row').slice(2); // skip thead rows
    const firstTeamBefore = rowsBefore[0].textContent;

    await user.click(ptsHeader);
    const rowsAfter = screen.getAllByRole('row').slice(2);
    const firstTeamAfter = rowsAfter[0].textContent;

    expect(firstTeamAfter).not.toBe(firstTeamBefore);
  });

  it('changes sort column when clicking a different header', async () => {
    const user = userEvent.setup();
    render(<LadderTable entries={entries} title="Ladder" showAverages={true} />);

    const pctHeader = screen.getByText(/^%/);
    await user.click(pctHeader);

    // Gamma has highest % (130.0), should appear first when sorting by % desc
    const tbody = screen.getAllByRole('rowgroup')[1]; // index 0 = thead, 1 = tbody
    const rows = tbody.querySelectorAll('tr');
    expect(rows[0].textContent).toContain('Gamma');
  });
});
