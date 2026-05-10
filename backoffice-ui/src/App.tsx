import { createBrowserRouter, RouterProvider } from 'react-router-dom';
import { Layout } from './components/Layout';
import { CreateOrderPage } from './pages/CreateOrderPage';
import { DashboardPage } from './pages/DashboardPage';
import { OrderTimelinePage } from './pages/OrderTimelinePage';
import { ToolsPage } from './pages/ToolsPage';

const router = createBrowserRouter([
  {
    path: '/',
    element: <Layout />,
    children: [
      { index: true, element: <DashboardPage /> },
      { path: 'orders', element: <CreateOrderPage /> },
      { path: 'timeline', element: <OrderTimelinePage /> },
      { path: 'tools', element: <ToolsPage /> }
    ]
  }
]);

export const App = () => <RouterProvider router={router} />;
