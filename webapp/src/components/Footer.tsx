const Footer = () => {
  return (
    <footer className="bg-white border-t border-gray-200 mt-auto">
      <div className="container-custom py-8">
        <p className="text-center text-gray-400 text-sm">
          &copy; {new Date().getFullYear()} FortuneStore. All rights reserved.
        </p>
      </div>
    </footer>
  );
};

export default Footer;
