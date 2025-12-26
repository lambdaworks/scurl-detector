const lightCodeTheme = require('prism-react-renderer').themes.github;
const darkCodeTheme = require('prism-react-renderer').themes.dracula;

const config = {
  title: 'Scala URL Detector',
  tagline: 'Scala library that detects and extracts URLs from text',
  url: 'https://lambdaworks.github.io',
  baseUrl: '/scurl-detector/',
  onBrokenLinks: 'throw',
  onBrokenMarkdownLinks: 'warn',
  favicon: 'img/favicon.png',
  organizationName: 'lambdaworks',
  projectName: 'scurl-detector',
  i18n: {
    defaultLocale: 'en',
    locales: ['en'],
  },
  presets: [
    [
      'classic',
      {
        docs: {
          path: '../scurl-detector-docs/target/mdoc',
          routeBasePath: '/',
          sidebarPath: require.resolve('./sidebars.js'),
          editUrl: ({ docPath }) =>
            `https://github.com/lambdaworks/scurl-detector/edit/main/docs/${docPath}`,
        },
        blog: false,
        theme: {
          customCss: require.resolve('./src/css/custom.css'),
        },
      },
    ],
  ],
  themeConfig: {
    navbar: {
      title: 'Scala URL Detector',
      logo: {
        alt: 'lambdaworks',
        src: 'img/logo.svg',
        srcDark: 'img/logo-dark.svg',
      },
      items: [
        {
          position: 'right',
          label: 'Overview',
          to: 'overview/overview_index',
        },
        {
          position: 'right',
          label: 'Contributing',
          to: 'code-of-conduct',
        },
        {
          position: 'right',
          label: 'Code of Conduct',
          to: 'code-of-conduct',
        },
        {
          href: 'https://github.com/lambdaworks/scurl-detector',
          label: 'GitHub',
          position: 'right',
        },
      ],
    },
    footer: {
      style: 'dark',
      logo: {
        alt: 'lambdaworks',
        src: 'img/logo-footer.svg',
        href: 'https://www.lambdaworks.io/',
      },
      links: [
        {
          title: 'GitHub',
          items: [
            {
              html: `
                <a href="https://github.com/lambdaworks/scurl-detector">
                  <img src="https://img.shields.io/github/stars/lambdaworks/scurl-detector?style=social" alt="github" />
                </a>
              `,
            },
          ],
        },
        {
          title: 'Additional resources',
          items: [
            {
              label: 'Scaladoc of Scala URL Detector',
              to: 'pathname:///scurl-detector/api/io/lambdaworks/detection/',
            },
          ],
        },
      ],
      copyright: `Copyright © ${new Date().getFullYear()} LambdaWorks d.o.o.`,
    },
    prism: {
      theme: lightCodeTheme,
      darkTheme: darkCodeTheme,
      additionalLanguages: ['java', 'scala'],
    },
  },
};

module.exports = config;
