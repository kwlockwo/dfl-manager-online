import { NavLink } from 'react-router-dom';

export default function Header() {
  const linkClass = ({ isActive }: { isActive: boolean }) =>
    `px-4 py-2 text-sm font-medium rounded hover:bg-gray-700 transition-colors ${
      isActive ? 'bg-gray-700 text-white' : 'text-gray-300'
    }`;

  return (
    <nav className="bg-gray-900 text-white shadow-md">
      <div className="max-w-screen-xl mx-auto px-4 flex items-center h-14 gap-6">
        <a href="/" className="flex items-center shrink-0">
          <img src="/images/dfl-logo.png" alt="DFL" className="h-8" />
        </a>
        <div className="flex gap-1">
          <NavLink to="/" end className={linkClass}>
            Ladder
          </NavLink>
          <NavLink to="/fixtures" className={linkClass}>
            Fixtures
          </NavLink>
          <NavLink to="/results" className={linkClass}>
            Results
          </NavLink>
        </div>
      </div>
    </nav>
  );
}
