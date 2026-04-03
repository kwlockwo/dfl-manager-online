export interface Ladder {
  round: number;
  teamCode: string;
  wins: number;
  losses: number;
  draws: number;
  pointsFor: number;
  pointsAgainst: number;
  averageFor: number;
  averageAgainst: number;
  pts: number;
  percentage: number;
  displayName: string;
  teamUri: string;
}

export interface GameFixture {
  game: number;
  homeTeam: string;
  awayTeam: string;
  homeTeamScore: number;
  awayTeamScore: number;
  homeTeamDisplayName: string;
  awayTeamDisplayName: string;
  resultsUri: string | null;
}

export interface RoundFixtures {
  round: number;
  games: GameFixture[];
}

export interface PlayerStats {
  kicks: number;
  handballs: number;
  disposals: number;
  marks: number;
  hitouts: number;
  freesFor: number;
  freesAgainst: number;
  tackles: number;
  goals: number;
  behinds: number;
  score: number;
  predictedScore: number;
  trend: number;
  scrapingStatus: string | null;
}

export interface SelectedPlayer {
  playerId: number;
  teamPlayerId: number;
  name: string;
  position: string;
  hasPlayer: boolean;
  scoreUsed: boolean;
  dnp: boolean;
  replacementInd: string | null;
  emgSort: number;
  stats: PlayerStats;
}

export interface TeamResults {
  teamCode: string;
  teamName: string;
  players: SelectedPlayer[];
  emergencies: SelectedPlayer[];
  score: number;
  currentPredictedScore: number;
  predictedScore: number;
  trend: number;
  emgInd: string | null;
}

export interface Results {
  round: number;
  game: number;
  homeTeam: TeamResults | null;
  awayTeam: TeamResults | null;
}

export interface GameMenu {
  game: number;
  homeTeam: string;
  awayTeam: string;
  active: boolean;
  resultsUri: string;
}

export interface RoundMenu {
  round: number;
  games: GameMenu[];
  active: boolean;
}
