{ baseURL, isProduction, theme, themesDir, ... }:

{
  inherit baseURL theme themesDir;

  title = "YABOB";
  author = {
    name = "james' blog";
    email = "blog@j4m3s.eu";
  };
  copyright = "james — All rights reserved";

  params = {
    dateFmt = "2006-01-02 15:04:05 -0700";
    description = "james's blog. Mainly about Ops and some other stuff.";
    subtitle = "Yet Another Boring Ops Blog";
  };

  enableRobotsTXT = true;
  footnoteReturnLinkContents = "↩";

  markup = {
    highlight = {
      guessSyntax = true;
    };
  };

  permalinks = {
    posts = "/:year/:month/:title/";
  };

  taxonomies = {
    categories = "categories";
    series = "series";
    tags = "tags";
  };

  buildDrafts = isProduction;
  buildExpired = isProduction;
  buildFuture = isProduction;

  layoutDir = ./layouts;

  menu = {
    main = [
      {
        identifier = "post";
        name = "Posts";
        title = "All posts";
        url = "/posts/";
        weight = 1;
      }
      {
        identifier = "categories";
        name = "Categories";
        title = "All categories";
        url = "/categories/";
        weight = 2;
      }
      {
        identifier = "tags";
        name = "Tags";
        title = "All tags";
        url = "/tags/";
        weight = 3;
      }
      {
        identifier = "series";
        name = "Series";
        title = "All series";
        url = "/series/";
        weight = 4;
      }
    ];
    meta = [
      {
        identifier = "categories";
        name = "Categories";
        weight = 1;
      }
      {
        identifier = "tags";
        name = "tags";
        weight = 2;
      }
      {
        identifier = "series";
        name = "Series";
        weight = 3;
      }
    ];
    footer = [
      {
        name = "GitHub";
        url = "https://github.com/j4m3s-s";
        weight = 1;
      }
      {
        name = "LinkedIn";
        url = "https://www.linkedin.com/in/james-landrein/";
        weight = 2;
      }
      {
        name = "Email";
        url = "mailto:blog@j4m3s.eu";
        weight = 3;
      }
    ];
  };
}
