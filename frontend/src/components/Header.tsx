import { NavLink } from 'react-router-dom';

export default function Header() {
  const linkClass = ({ isActive }: { isActive: boolean }) =>
    `px-4 py-1.5 text-sm font-medium rounded transition-colors ${
      isActive ? 'bg-gray-300 text-gray-900' : 'text-gray-700 hover:bg-gray-200'
    }`;

  return (
    <nav className="bg-gray-100 border-b border-gray-300 shadow-sm">
      <div className="px-4 flex items-center h-12 gap-4">
        <a href="/" className="flex items-center shrink-0">
          <img src="/images/dfl-logo.png" alt="DFL" className="h-10" />
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
