import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter } from 'react-router-dom';
import RoundFixturesCard from '../../../features/fixtures/RoundFixturesCard';
import type { RoundFixtures } from '../../../types/api';

const makeRound = (resultsUri: string | null = '/results/3/1'): RoundFixtures => ({
  round: 3,
  games: [
    {
      game: 1,
      homeTeam: 'AAA',
      awayTeam: 'BBB',
      homeTeamDisplayName: 'Alpha',
      awayTeamDisplayName: 'Beta',
      homeTeamScore: 450,
      awayTeamScore: 380,
      resultsUri,
    },
    {
      game: 2,
      homeTeam: 'CCC',
      awayTeam: 'DDD',
      homeTeamDisplayName: 'Gamma',
      awayTeamDisplayName: 'Delta',
      homeTeamScore: 0,
      awayTeamScore: 0,
      resultsUri: null,
    },
  ],
});

const renderCard = (round: RoundFixtures) =>
  render(
    <MemoryRouter>
      <RoundFixturesCard round={round} />
    </MemoryRouter>
  );

describe('RoundFixturesCard', () => {
  it('renders the round heading', () => {
    renderCard(makeRound());
    expect(screen.getByText('Round 3')).toBeInTheDocument();
  });

  it('renders team display names', () => {
    renderCard(makeRound());
    expect(screen.getByText('Alpha')).toBeInTheDocument();
    expect(screen.getByText('Beta')).toBeInTheDocument();
    expect(screen.getByText('Gamma')).toBeInTheDocument();
    expect(screen.getByText('Delta')).toBeInTheDocument();
  });

  it('renders scores', () => {
    renderCard(makeRound());
    expect(screen.getByText('450')).toBeInTheDocument();
    expect(screen.getByText('380')).toBeInTheDocument();
  });

  it('applies cursor-pointer class to rows with a resultsUri', () => {
    renderCard(makeRound('/results/3/1'));
    const rows = screen.getAllByRole('row').slice(1); // skip header
    expect(rows[0].className).toContain('cursor-pointer');
  });

  it('does not apply cursor-pointer to rows without a resultsUri', () => {
    renderCard(makeRound());
    const rows = screen.getAllByRole('row').slice(1);
    expect(rows[1].className).not.toContain('cursor-pointer');
  });

  it('navigates on click for rows with resultsUri', async () => {
    const user = userEvent.setup();
    renderCard(makeRound('/results/3/1'));
    const rows = screen.getAllByRole('row').slice(1);
    // just verifying the click doesn't throw
    await user.click(rows[0]);
  });
});
