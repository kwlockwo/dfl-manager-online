import type { Ladder, RoundFixtures, Results, RoundMenu } from '../types/api';

const JSON_HEADERS = { Accept: 'application/json' };

async function get<T>(url: string): Promise<T> {
  const res = await fetch(url, { headers: JSON_HEADERS });
  if (!res.ok) throw new Error(`${res.status} ${res.statusText} (${url})`);
  return res.json() as Promise<T>;
}

export const fetchLadder = () => get<Ladder[]>('/ladder');
export const fetchLiveLadder = () => get<Ladder[]>('/ladder/live');
export const fetchFixtures = () => get<RoundFixtures[]>('/fixtures');
export const fetchCurrentResults = () => get<Results>('/results');
export const fetchResults = (round: number, game: number) =>
  get<Results>(`/results/${round}/${game}`);
export const fetchResultsMenu = (round?: number, game?: number) => {
  const params =
    round != null && game != null ? `?round=${round}&game=${game}` : '';
  return get<RoundMenu[]>(`/results/menu${params}`);
};
