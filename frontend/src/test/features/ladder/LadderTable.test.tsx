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

// Alpha: 8pts, 110% — last by default
// Beta:  12pts, 125%, 600for
// Gamma: 12pts, 130%, 550for — first by default (same pts as Beta, higher %)
const entries: Ladder[] = [
  makeEntry('AAA', 'Alpha', 8, 110, 500),
  makeEntry('BBB', 'Beta', 12, 125, 600),
  makeEntry('CCC', 'Gamma', 12, 130, 550),
];

function getBodyRows() {
  const tbody = screen.getAllByRole('rowgroup')[1];
  return Array.from(tbody.querySelectorAll('tr'));
}

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
    expect(screen.getByText('110.00')).toBeInTheDocument();
  });

  it('applies default sort: Pts desc then % desc then For desc', () => {
    render(<LadderTable entries={entries} title="Ladder" showAverages={true} />);
    const rows = getBodyRows();
    // Gamma (12pts, 130%) > Beta (12pts, 125%) > Alpha (8pts)
    expect(rows[0].textContent).toContain('Gamma');
    expect(rows[1].textContent).toContain('Beta');
    expect(rows[2].textContent).toContain('Alpha');
  });

  it('does not show reset button when on default sort', () => {
    render(<LadderTable entries={entries} title="Ladder" showAverages={true} />);
    expect(screen.queryByText('Reset sort')).not.toBeInTheDocument();
  });

  it('single click on % sorts by % desc only', async () => {
    const user = userEvent.setup();
    render(<LadderTable entries={entries} title="Ladder" showAverages={true} />);
    await user.click(screen.getByText(/^%/));
    const rows = getBodyRows();
    expect(rows[0].textContent).toContain('Gamma'); // 130%
    expect(rows[1].textContent).toContain('Beta');  // 125%
    expect(rows[2].textContent).toContain('Alpha'); // 110%
  });

  it('single click toggles direction on second click', async () => {
    const user = userEvent.setup();
    render(<LadderTable entries={entries} title="Ladder" showAverages={true} />);
    const pctHeader = screen.getByText(/^%/);
    await user.click(pctHeader);
    await user.click(pctHeader);
    const rows = getBodyRows();
    expect(rows[0].textContent).toContain('Alpha'); // 110% lowest
  });

  it('shows reset button after changing sort', async () => {
    const user = userEvent.setup();
    render(<LadderTable entries={entries} title="Ladder" showAverages={true} />);
    await user.click(screen.getByText(/^%/));
    expect(screen.getByText('Reset sort')).toBeInTheDocument();
  });

  it('reset button restores default sort', async () => {
    const user = userEvent.setup();
    render(<LadderTable entries={entries} title="Ladder" showAverages={true} />);
    await user.click(screen.getByText(/^%/));
    await user.click(screen.getByText('Reset sort'));
    const rows = getBodyRows();
    expect(rows[0].textContent).toContain('Gamma');
    expect(screen.queryByText('Reset sort')).not.toBeInTheDocument();
  });
});
