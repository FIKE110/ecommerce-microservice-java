import { Link } from 'react-router-dom';

const HomePage = () => {
  return (
    <div className="bg-white">
      <div className="relative overflow-hidden">
        <div className="container-custom py-16 sm:py-24 lg:py-32">
          <div className="relative max-w-xl mx-auto px-4 sm:px-6 lg:px-8 sm:text-center">
            <h1 className="text-4xl font-extrabold text-gray-900 tracking-tight sm:text-5xl md:text-6xl">
              <span className="block xl:inline">Welcome to </span>
              <span className="block text-indigo-600 xl:inline">FortuneStore</span>
            </h1>
            <p className="mt-3 text-xl text-gray-500 sm:mt-5 max-w-prose mx-auto">
              Discover the best products at unbeatable prices. Shop the latest trends in electronics, fashion, and home goods.
            </p>
            <div className="mt-10 sm:flex sm:justify-center">
              <div className="rounded-md shadow">
                <Link
                  to="/products"
                  className="w-full flex items-center justify-center px-8 py-3 border border-transparent text-base font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 md:py-4 md:text-lg md:px-10"
                >
                  Shop Now
                </Link>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default HomePage;
