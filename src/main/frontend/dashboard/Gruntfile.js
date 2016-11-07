/*jslint node: true */
'use strict';

var pkg = require('./package.json');

//Using exclusion patterns slows down Grunt significantly
//instead of creating a set of patterns like '**/*.js' and '!**/node_modules/**'
//this method is used to create a set of inclusive patterns for all subdirectories
//skipping node_modules, bower_components, dist, and any .dirs
//This enables users to create any directory structure they desire.
var createFolderGlobs = function (fileTypePatterns) {
  fileTypePatterns = Array.isArray(fileTypePatterns) ? fileTypePatterns : [fileTypePatterns];
  var ignore = ['node_modules', 'bower_components', 'dist', 'temp'];
  var fs = require('fs');
  return fs.readdirSync(process.cwd())
      .map(function (file) {
        if (ignore.indexOf(file) !== -1 ||
            file.indexOf('.') === 0 || !fs.lstatSync(file).isDirectory()) {
          return null;
        } else {
          return fileTypePatterns.map(function (pattern) {
            return file + '/**/' + pattern;
          });
        }
      })
      .filter(function (patterns) {
        return patterns;
      })
      .concat(fileTypePatterns);
};

module.exports = function (grunt) {

  // load all grunt tasks
  require('load-grunt-tasks')(grunt);

  // Project configuration.
  grunt.initConfig({
    connect: {
      main: {
        options: {
          port: 9001,
          // hostname: '0.0.0.0',
          open: true
        }
      }
    },
    watch: {
      main: {
        options: {
          livereload: 35730,
          livereloadOnError: false,
          spawn: false
        },
        files: [createFolderGlobs(['*.js', '*.less', '*.html']), '!_SpecRunner.html', '!.grunt'],
        tasks: [] //all the tasks are run dynamically during the watch event handler
      }
    },
    jshint: {
      main: {
        options: {
          jshintrc: '.jshintrc'
        },
        src: createFolderGlobs('*.js')
      }
    },
    clean: {
      before: {
        src: ['dist', 'temp']
      },
      after: {
        src: ['temp']
      }
    },
    less: {
      production: {
        options: {},
        files: {
          'temp/app.css': 'app.less'
        }
      }
    },
    ngtemplates: {
      main: {
        options: {
          module: pkg.name,
          htmlmin: '<%= htmlmin.main.options %>'
        },
        src: [createFolderGlobs('*.html'), '!index.html', '!_SpecRunner.html'],
        dest: 'temp/templates.js'
      }
    },
    copy: {
      main: {
        files: [
          {
            src: [
              'img/**'
            ],
            dest: 'dist/'
          },
          {
            src: [
              'config.js'
            ],
            dest: 'dist/config.js'
          },
          { // Semantic UI
            src: [
              'bower_components/semantic/dist/themes/default/assets/fonts/icons.woff'
            ],
            dest: 'dist/themes/default/assets/fonts/',
            filter: 'isFile',
            flatten: true,
            expand: true
          },
          { // tinymce
            cwd: 'bower_components/tinymce-dist/',
            src: [
              'themes/modern/theme.min.js', 'skins/lightgray/content.min.css', 'skins/lightgray/skin.min.css',
              'skins/lightgray/fonts/tinymce.woff'
            ],
            dest: 'dist/',
            filter: 'isFile',
            expand: true
          }

        ]
      }
    },
    dom_munger: {
      read: {
        options: {
          read: [
            {selector: 'script[data-concat!="false"]', attribute: 'src', writeto: 'appjs'},
            {
              selector: 'link[rel="stylesheet"][data-concat!="false"]',
              attribute: 'href',
              writeto: 'vendorcss',
              isPath: true
            }
          ]
        },
        src: 'index.html'
      },
      update: {
        options: {
          remove: ['script[data-remove!="false"]', 'link[data-remove!="false"]'],
          append: [
            {selector: 'head', html: '<link rel="stylesheet" href="vendor.full.min.css">'},
            {selector: 'head', html: '<link rel="stylesheet" href="app.full.min.css">'},
            {selector: 'head', html: '<script src="app.full.min.js"></script>'}
          ]
        },
        src: 'index.html',
        dest: 'dist/index.html'
      }
    },
    cssmin: {
      main: {
        src: ['temp/app.css'],
        dest: 'dist/app.full.min.css'
      },
      vendor: {
        src: ['<%= dom_munger.data.vendorcss %>'],
        dest: 'dist/vendor.full.min.css'
      }
    },
    concat: {
      main: {
        src: ['<%= dom_munger.data.appjs %>', '<%= ngtemplates.main.dest %>'],
        dest: 'temp/app.full.js'
      }
    },
    ngAnnotate: {
      main: {
        src: 'temp/app.full.js',
        dest: 'temp/app.full.js'
      }
    },
    uglify: {
      main: {
        src: 'temp/app.full.js',
        dest: 'dist/app.full.min.js'
      }
    },
    htmlmin: {
      main: {
        options: {},
        files: {
          'dist/index.html': 'dist/index.html'
        }
      }
    },
    ngconstant: {
      options: {
        name: 'clickTracker',
        wrap: '"use strict";\n\n{%= __ngModule %}',
        space: '  '
      },
      development: {
        options: {
          deps: false,
          dest: 'scripts/config.js'
        },
        constants: {
          IP: 'http://localhost:8080/adserver'
        }

      },
      production: {
        options: {
          deps: false,
          dest: 'config.js'
        },
        constants: {
          IP: 'http://api.codedesign.si'
        }
      }
    },
    //Imagemin has issues on Windows.
    //To enable imagemin:
    // - "npm install grunt-contrib-imagemin"
    // - Comment in this section
    // - Add the "imagemin" task after the "htmlmin" task in the build task alias
    // imagemin: {
    //   main:{
    //     files: [{
    //       expand: true, cwd:'dist/',
    //       src:['**/{*.png,*.jpg}'],
    //       dest: 'dist/'
    //     }]
    //   }
    // },
    karma: {
      options: {
        frameworks: ['jasmine'],
        files: [  //this files data is also updated in the watch handler, if updated change there too
          '<%= dom_munger.data.appjs %>',
          'bower_components/angular-mocks/angular-mocks.js',
          createFolderGlobs('*-spec.js')
        ],
        logLevel: 'ERROR',
        reporters: ['mocha'],
        autoWatch: false, //watching is handled by grunt-contrib-watch
        singleRun: true
      },
      all_tests: {
        browsers: ['PhantomJS', 'Chrome', 'Firefox']
      },
      during_watch: {
        browsers: ['PhantomJS']
      },
    }
  });

  grunt.registerTask('build', ['ngconstant:production', 'clean:before', 'less', 'dom_munger', 'ngtemplates', 'cssmin', 'concat', 'ngAnnotate', 'uglify', 'copy', 'htmlmin', 'clean:after']);
  grunt.registerTask('serve', ['ngconstant:development', 'dom_munger:read', 'connect', 'watch']);
  grunt.registerTask('test', ['dom_munger:read', 'karma:all_tests']);


};
