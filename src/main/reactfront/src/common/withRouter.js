// withRouter.js
import { useNavigate } from 'react-router-dom';

function withRouter(Component) {
  return (props) => {
    const navigate = useNavigate();
    return <Component {...props} navigate={navigate} />;
  };
}

export default withRouter;