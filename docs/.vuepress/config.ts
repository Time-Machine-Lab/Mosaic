import { defineUserConfig } from 'vuepress'
import { defaultTheme } from '@vuepress/theme-default'
import { viteBundler } from '@vuepress/bundler-vite'
import { path } from '@vuepress/utils'

export default defineUserConfig({
  lang: 'zh-CN',
  title: 'Mosaic Framework',
  description: '基于插件化架构的Java模块化框架',
  base: '/Mosaic/',
  public: path.resolve(__dirname, '../public'),
  bundler: viteBundler({
    viteOptions: {},
    vuePluginOptions: {},
  }),
  
  theme: defaultTheme({
    logo: '/logo.png',
    navbar: [
      {
        text: '指南',
        link: '/guide/',
      },
      {
        text: 'API参考',
        link: '/api/',
      },
      {
        text: '插件开发',
        link: '/plugin/',
      },
      {
        text: '示例',
        link: '/examples/',
      },
      {
        text: 'GitHub',
        link: 'https://github.com/Time-Machine-Lab/Mosaic'
      }
    ],
    sidebar: {
      '/guide/': [
        {
          text: '指南',
          children: [
            '/guide/README.md',
            '/guide/getting-started.md',
            '/guide/core-concepts.md',
            '/guide/configuration.md',
            '/guide/event-system.md'
          ]
        }
      ],
      '/api/': [
        {
          text: 'API参考',
          children: [
            '/api/README.md',
            '/api/cube-api.md',
            '/api/extension-api.md',
            '/api/event-api.md',
            '/api/config-api.md'
          ]
        }
      ],
      '/plugin/': [
        {
          text: '插件开发',
          children: [
            '/plugin/README.md',
            '/plugin/cube-development.md',
            '/plugin/extension-development.md',
            '/plugin/listener-development.md',
            '/plugin/best-practices.md'
          ]
        }
      ],
      '/examples/': [
        {
          text: '示例',
          children: [
            '/examples/README.md',
            '/examples/ai-chat-plugin.md',
            '/examples/system-log-plugin.md',
            '/examples/listener-plugin.md'
          ]
        }
      ]
    }
  })
})