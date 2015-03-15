/*
 * Copyright (C) 2014 Xform-Live contributors
 *
 * This file is part of Xform-Live.
 *
 * Xform-Live is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * Xform-Live is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Xform-Live.  If not, see <http://www.gnu.org/licenses/>.
 */
var xform_live = angular.module('xform-live', ['ui.ace', 'ngResource']);
xform_live.factory('Config', ['$resource', function($resource) {
    return $resource('rest/xform/config/:uuid', {uuid:'@uuid'});
}]);
xform_live.factory('SourceFile', ['$resource', function($resource) {
    return $resource('rest/xform/file/:uuid/source_files', {fullPath:'@fullPath'});
}]);
xform_live.factory('TransformationFile', ['$resource', function($resource) {
    return $resource('rest/xform/file/:uuid/transformation_files', {fullPath:'@fullPath'});
}]);
xform_live.controller('dummy_controller', function($scope, $http, $interval, $location, $q, Config, SourceFile, TransformationFile) {
    $scope.config_list = [];
    $scope.selected_builtin;
    $scope.selected = {};
    $scope.source_contents = {};
    $scope.pending_transforms = {};
    $scope.transformed = {};
    $scope.new_directory = {};
    $scope.selected_transformations = [];
    $scope.editor_init = function(editor) {
        function resize(editor) {
            var rows = editor.getSession().getScreenLength();
            var line_height = editor.renderer.lineHeight;
            $(editor.renderer.container).height((Math.min(rows,50) + 2) * line_height);
            editor.resize();
        }
        resize(editor);
        editor.on('change', function() { resize(editor); });
    };
    $scope.load_config = function() {
        var file = $('#config-file').get(0).files[0];
        console.log('loading config from ' + file.name);
        var reader = new FileReader();
        reader.onload = function(event) {
            var config = JSON.parse(event.target.result);
            config.uuid = $scope.config.uuid;
            Config.save(config, function() {
                $scope.config = config;
                $('#load-config').modal('hide');
                $scope.refresh();
            });
        }
        reader.readAsText(file);
    };
    $scope.update_config = function() {
        Config.save($scope.config, function() {
            $scope.refresh();
            $scope.new_directory.fullPath = null;
            $scope.new_directory.recursive = false;
        });
    };
    $scope.config_download = function() {
        return '/rest/xform/config/' + $scope.config.uuid + '/download';
    };
    $scope.refresh = function() {
        if ($scope.config_list.length == 0) {
            $http.get('/rest/xform/config/builtin').success(function(data) {
                $scope.config_list = data;
            });;
        }
        function refresh_after_config() {
            $location.hash($scope.config.uuid);
            $scope.source_files = SourceFile.query({uuid: $scope.config.uuid});
            $scope.transformations = TransformationFile.query({uuid: $scope.config.uuid});
            for (var path in $scope.selected) {
                $scope.refresh_views(path, true);
            }
        }
        if ($scope.config) {
            refresh_after_config();
        }
        else {
            $scope.config = Config.get({uuid: $location.hash()}, function() {
                refresh_after_config();
            });
        }
    };
    $scope.refresh_views = function(path, force_refresh) {
        if ($scope.selected[path] && force_refresh || !(path in $scope.source_contents)) {
            $scope.contents(path);
        }
    };
    $scope.add_directory = function(destination) {
        $scope.config[destination].push({
            fullPath: $scope.new_directory.fullPath,
            recursive: $scope.new_directory.recursive
        });
    };
    $scope.remove_directory = function(destination, index) {
        $scope.config[destination].splice(index,1);
    };
    $scope.contents = function(fullPath) {
        return $http.get('/rest/xform/file/' + $scope.config.uuid + '/contents', {params: {uuid: $scope.config.uuid, fullPath: fullPath}}).success(function(data) {
            $scope.source_contents[fullPath] = data;
            $scope.transform(fullPath);
        });
    };
    $scope.transform = function(fullPath, $index) {
        if (typeof($index) === 'undefined') {
            $index = 0;
        }
        console.log('transforming w/ transform index', fullPath, $index);
        if (!$scope.selected_transformations[$index]) {
            return;
        }
        var contents;
        if ($index == 0) {
            contents = $scope.source_contents[fullPath];
        }
        else {
            contents = $scope.transformed[$index - 1][fullPath].contents;
        }
        if ($index in $scope.pending_transforms) {
            var request_cancel = $scope.pending_transforms[$index];
            request_cancel.resolve();
        }
        var request_cancel = $q.defer();
        $scope.pending_transforms[$index] = request_cancel;
        $http.post('/rest/xform/transform/' + $scope.config.uuid, contents, {params: {transformation: $scope.selected_transformations[$index]}, timeout: request_cancel.promise}).success(function(transformed) {
            $scope.transformed[$index][fullPath] = {
                transformation: $scope.selected_transformations[$index],
                contents: transformed,
                error: null,
                successful: true
            };
            if ($index + 1 < $scope.selected_transformations.length) {
                $scope.transform(fullPath, $index + 1);
            }
            delete $scope.pending_transforms[$index];
        }).error(function(data, status) {
            for (var i = $index; i < $scope.selected_transformations.length; i++) {
                $scope.transformed[i][fullPath] = {
                    transformation: null,
                    contents: '',
                    error: data,
                    successful: false
                }
            }
            $scope.transformed[$index][fullPath].transformation = $scope.selected_transformations[$index];
            if (status != 0) { // weren't aborted
                delete $scope.pending_transforms[$index];
            }
        })
    };
    $scope.add_transformation = function() {
        $scope.selected_transformations.push(null);
        $scope.transformed[$scope.selected_transformations.length - 1] = {};
    }
    $scope.remove_transformation = function() {
        delete $scope.transformed[$scope.selected_transformations.length - 1];
        $scope.selected_transformations.pop();
    }
    $scope.change_transform = function($index) {
        for (path in $scope.source_contents) {
            if ($scope.selected[path] && !($scope.transformed[$index][path]) || $scope.transformed[$index][path].transformation != $scope.selected_transformations[$index]) {
                $scope.transform(path, $index);
            }
        }
    }
    $scope.toggle_refresh_job = function() {
        if ($scope.autorefresh) {
            $scope.refresh_job = $scope.create_refresh_job();
        }
        else {
            $interval.cancel($scope.refresh_job);
        }
    };
    $scope.create_refresh_job = function() {
        return $interval(function() {
            for (path in $scope.selected) {
                if ($scope.selected[path]) {
                    $scope.transform(path);
                }
            }
        }, 1000);
    };
    $scope.load_builtin = function() {
        $scope.config = JSON.parse($scope.selected_builtin);
        $scope.refresh();
    }
});
