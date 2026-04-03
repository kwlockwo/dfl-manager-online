import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter } from 'react-router-dom';
import ResultsSidebar from '../../../features/results/ResultsSidebar';
import type { RoundMenu } from '../../../types/api';

const makeMenu = (): RoundMenu[] => [
  {
    round: 2,
    active: false,
    games: [
      { game: 1, homeTeam: 'AAA', awayTeam: 'BBB', active: false, resultsUri: '/results/2/1' },
    ],
  },
  {
    round: 3,
    active: true,
    games: [
      { game: 1, homeTeam: 'CCC', awayTeam: 'DDD', active: true, resultsUri: '/results/3/1' },
      { game: 2, homeTeam: 'EEE', awayTeam: 'FFF', active: false, resultsUri: '/results/3/2' },
    ],
  },
];

const renderSidebar = (menu: RoundMenu[]) =>
  render(
    <MemoryRouter>
      <ResultsSidebar menu={menu} />
    </MemoryRouter>
  );

describe('ResultsSidebar', () => {
  it('renders a heading for each round', () => {
    renderSidebar(makeMenu());
    expect(screen.getByText('Round 2')).toBeInTheDocument();
    expect(screen.getByText('Round 3')).toBeInTheDocument();
  });

  it('expands the active round by default', () => {
    renderSidebar(makeMenu());
    expect(screen.getByText('CCC v DDD')).toBeInTheDocument();
    expect(screen.getByText('EEE v FFF')).toBeInTheDocument();
  });

  it('does not expand inactive rounds by default', () => {
    renderSidebar(makeMenu());
    expect(screen.queryByText('AAA v BBB')).not.toBeInTheDocument();
  });

  it('expands a collapsed round on click', async () => {
    const user = userEvent.setup();
    renderSidebar(makeMenu());
    await user.click(screen.getByText('Round 2'));
    expect(screen.getByText('AAA v BBB')).toBeInTheDocument();
  });

  it('collapses an expanded round on click', async () => {
    const user = userEvent.setup();
    renderSidebar(makeMenu());
    await user.click(screen.getByText('Round 3'));
    expect(screen.queryByText('CCC v DDD')).not.toBeInTheDocument();
  });

  it('applies bold styling to the active game link', () => {
    renderSidebar(makeMenu());
    const activeLink = screen.getByText('CCC v DDD');
    expect(activeLink.className).toContain('font-bold');
  });

  it('does not apply bold styling to inactive game links', async () => {
    const user = userEvent.setup();
    renderSidebar(makeMenu());
    await user.click(screen.getByText('Round 2'));
    const inactiveLink = screen.getByText('AAA v BBB');
    expect(inactiveLink.className).not.toContain('font-bold');
  });

  it('renders correct hrefs for game links', async () => {
    const user = userEvent.setup();
    renderSidebar(makeMenu());
    await user.click(screen.getByText('Round 2'));
    const link = screen.getByText('AAA v BBB').closest('a');
    expect(link).toHaveAttribute('href', '/results/2/1');
  });

  it('renders an empty sidebar gracefully', () => {
    renderSidebar([]);
    expect(screen.getByText('Fixtures')).toBeInTheDocument();
  });
});
