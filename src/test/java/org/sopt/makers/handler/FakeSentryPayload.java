package org.sopt.makers.handler;

public final class FakeSentryPayload {
	public static final String REAL_SENTRY_WEBHOOK_PAYLOAD = """
		{
		   "action": "triggered",
		   "installation": {
		     "uuid": "14570066-3c21-4c4d-8e9d-78e4995df1f3"
		   },
		   "data": {
		     "event": {
		       "event_id": "93153fe001674c70b39af22c49db350b",
		       "project": 9999999999999999,
		       "release": "x.y.z",
		       "dist": null,
		       "platform": "other",
		       "message": "Authentication failed, token expired!",
		       "datetime": "2025-05-21T08:52:45.182000Z",
		       "tags": [
		         [
		           "browser",
		           "Chrome 60.0.3112"
		         ],
		         [
		           "browser.name",
		           "Chrome"
		         ],
		         [
		           "client_os",
		           "Mac OS X 10.12.6"
		         ],
		         [
		           "client_os.name",
		           "Mac OS X"
		         ],
		         [
		           "device",
		           "Mac"
		         ],
		         [
		           "device.family",
		           "Mac"
		         ],
		         [
		           "environment",
		           "production"
		         ],
		         [
		           "level",
		           "error"
		         ],
		         [
		           "os",
		           "Mac OS X 10.12.6"
		         ],
		         [
		           "sample_event",
		           "yes"
		         ],
		         [
		           "release",
		           "x.y.z"
		         ],
		         [
		           "user",
		           "username:anonymous"
		         ],
		         [
		           "server_name",
		           "server-example.com"
		         ],
		         [
		           "url",
		           "http://localhost:8080/"
		         ]
		       ],
		       "_meta": {
		         "platform": {
		           "": {
		             "val": "java-logback"
		           }
		         }
		       },
		       "_metrics": {
		         "bytes.stored.event": 33577
		       },
		       "_ref": 9999999999999999,
		       "_ref_version": 2,
		       "breadcrumbs": {
		         "values": [
		           {
		             "level": "debug",
		             "message": "Querying for user.",
		             "timestamp": 1501799139,
		             "type": "default"
		           },
		           {
		             "level": "debug",
		             "message": "User found: anonymous@example.com",
		             "timestamp": 1501799139,
		             "type": "default"
		           },
		           {
		             "level": "info",
		             "message": "Loaded homepage content from memcached.",
		             "timestamp": 1501799139,
		             "type": "default"
		           },
		           {
		             "level": "warning",
		             "message": "Sidebar content not in cache, hitting API server.",
		             "timestamp": 1501799139,
		             "type": "default"
		           }
		         ]
		       },
		       "contexts": {
		         "browser": {
		           "browser": "Chrome 60.0.3112",
		           "name": "Chrome",
		           "type": "browser",
		           "version": "60.0.3112"
		         },
		         "client_os": {
		           "name": "Mac OS X",
		           "os": "Mac OS X 10.12.6",
		           "type": "os",
		           "version": "10.12.6"
		         },
		         "device": {
		           "brand": "Apple",
		           "family": "Mac",
		           "model": "Mac",
		           "type": "device"
		         }
		       },
		       "culprit": "io.example.ApiRequest in perform",
		       "environment": "production",
		       "exception": {
		         "values": [
		           {
		             "module": "io.example",
		             "stacktrace": {
		               "frames": [
		                 {
		                   "abs_path": "Thread.java",
		                   "addr_mode": null,
		                   "colno": null,
		                   "context_line": null,
		                   "data": {
		                     "category": "std",
		                     "client_in_app": false
		                   },
		                   "errors": null,
		                   "filename": "Thread.java",
		                   "function": "run",
		                   "image_addr": null,
		                   "in_app": false,
		                   "instruction_addr": null,
		                   "lineno": 748,
		                   "lock": null,
		                   "module": "java.lang.Thread",
		                   "package": null,
		                   "platform": null,
		                   "post_context": null,
		                   "pre_context": null,
		                   "raw_function": null,
		                   "source_link": null,
		                   "symbol": null,
		                   "symbol_addr": null,
		                   "trust": null,
		                   "vars": null
		                 },
		                 {
		                   "abs_path": "Application.java",
		                   "addr_mode": null,
		                   "colno": null,
		                   "context_line": null,
		                   "data": {
		                     "category": "framework",
		                     "client_in_app": true,
		                     "orig_in_app": 1
		                   },
		                   "errors": null,
		                   "filename": "Application.java",
		                   "function": "home",
		                   "image_addr": null,
		                   "in_app": false,
		                   "instruction_addr": null,
		                   "lineno": 102,
		                   "lock": null,
		                   "module": "io.example.Application",
		                   "package": null,
		                   "platform": null,
		                   "post_context": null,
		                   "pre_context": null,
		                   "raw_function": null,
		                   "source_link": null,
		                   "symbol": null,
		                   "symbol_addr": null,
		                   "trust": null,
		                   "vars": null
		                 },
		                 {
		                   "abs_path": "Sidebar.java",
		                   "addr_mode": null,
		                   "colno": null,
		                   "context_line": null,
		                   "data": {
		                     "category": "framework",
		                     "client_in_app": true,
		                     "orig_in_app": 1
		                   },
		                   "errors": null,
		                   "filename": "Sidebar.java",
		                   "function": "fetch",
		                   "image_addr": null,
		                   "in_app": false,
		                   "instruction_addr": null,
		                   "lineno": 5,
		                   "lock": null,
		                   "module": "io.example.Sidebar",
		                   "package": null,
		                   "platform": null,
		                   "post_context": null,
		                   "pre_context": null,
		                   "raw_function": null,
		                   "source_link": null,
		                   "symbol": null,
		                   "symbol_addr": null,
		                   "trust": null,
		                   "vars": null
		                 },
		                 {
		                   "abs_path": "ApiRequest.java",
		                   "addr_mode": null,
		                   "colno": null,
		                   "context_line": null,
		                   "data": {
		                     "category": "framework",
		                     "client_in_app": true,
		                     "orig_in_app": 1
		                   },
		                   "errors": null,
		                   "filename": "ApiRequest.java",
		                   "function": "perform",
		                   "image_addr": null,
		                   "in_app": false,
		                   "instruction_addr": null,
		                   "lineno": 8,
		                   "lock": null,
		                   "module": "io.example.ApiRequest",
		                   "package": null,
		                   "platform": null,
		                   "post_context": null,
		                   "pre_context": null,
		                   "raw_function": null,
		                   "source_link": null,
		                   "symbol": null,
		                   "symbol_addr": null,
		                   "trust": null,
		                   "vars": null
		                 }
		               ]
		             },
		             "type": "ApiException",
		             "value": "Authentication failed, token expired!"
		           }
		         ]
		       },
		       "extra": {
		         "emptyList": [],
		         "emptyMap": {},
		         "length": 10837790,
		         "results": [
		           1,
		           2,
		           3,
		           4,
		           5
		         ],
		         "session": {
		           "foo": "bar"
		         },
		         "unauthorized": false,
		         "url": "http://example.org/foo/bar/"
		       },
		       "fingerprint": [
		         "{{ default }}"
		       ],
		       "grouping_config": {
		         "enhancements": "KLUv_SAYwQAAkwKRs25ld3N0eWxlOjIwMjMtMDEtMTGQ",
		         "id": "newstyle:2023-01-11"
		       },
		       "hashes": [
		         "438f4d33ad9cb76a7531995b59d34c96"
		       ],
		       "level": "error",
		       "location": "ApiRequest.java",
		       "logentry": {
		         "formatted": "Authentication failed, token expired!",
		         "message": null,
		         "params": null
		       },
		       "logger": "",
		       "metadata": {
		         "filename": "ApiRequest.java",
		         "function": "perform",
		         "in_app_frame_mix": "system-only",
		         "type": "ApiException",
		         "value": "Authentication failed, token expired!"
		       },
		       "modules": {
		         "my.package": "1.0.0"
		       },
		       "nodestore_insert": 1747817625.45355,
		       "received": 1747817625.183474,
		       "request": {
		         "api_target": null,
		         "cookies": null,
		         "data": {
		           "logged_in": [
		             "1"
		           ]
		         },
		         "env": {
		           "AUTH_TYPE": null,
		           "LOCAL_ADDR": "0:0:0:0:0:0:0:1",
		           "LOCAL_NAME": "localhost",
		           "LOCAL_PORT": 8080,
		           "REMOTE_ADDR": "0:0:0:0:0:0:0:1",
		           "REMOTE_USER": null,
		           "REQUEST_ASYNC": false,
		           "REQUEST_SECURE": false,
		           "SERVER_NAME": "localhost",
		           "SERVER_PORT": 8080,
		           "SERVER_PROTOCOL": "HTTP/1.1"
		         },
		         "fragment": null,
		         "headers": [
		           [
		             "Accept-Language",
		             "en-US,en;q=0.8"
		           ],
		           [
		             "Host",
		             "localhost:8080"
		           ],
		           [
		             "Upgrade-Insecure-Requests",
		             "1"
		           ],
		           [
		             "Connection",
		             "keep-alive"
		           ],
		           [
		             "Dnt",
		             "1"
		           ],
		           [
		             "Cache-Control",
		             "max-age=0"
		           ],
		           [
		             "Accept-Encoding",
		             "gzip, deflate, br"
		           ],
		           [
		             "User-Agent",
		             "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.78 Safari/537.36"
		           ],
		           [
		             "Accept",
		             "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8"
		           ]
		         ],
		         "inferred_content_type": "application/json",
		         "method": "GET",
		         "query_string": [
		           [
		             "logged_in",
		             "1"
		           ]
		         ],
		         "url": "http://localhost:8080/"
		       },
		       "sdk": {
		         "integrations": null,
		         "name": "sentry-java",
		         "packages": null,
		         "version": "1.4.0-3ded0"
		       },
		       "timestamp": 1747817565.182,
		       "title": "ApiException: Authentication failed, token expired!",
		       "type": "error",
		       "user": {
		         "data": {
		           "account_level": "premium"
		         },
		         "email": "anonymous@example.com",
		         "ip_address": "0:0:0:0:0:0:0:1",
		         "sentry_user": "username:anonymous",
		         "username": "anonymous"
		       },
		       "version": "5",
		       "url": "https://sentry.io/api/0/projects/example-org/example-project/events/93153fe001674c70b39af22c49db350b/",
		       "web_url": "https://sentry.io/organizations/example-org/issues/0000000000/events/93153fe001674c70b39af22c49db350b/",
		       "issue_url": "https://sentry.io/api/0/organizations/example-org/issues/0000000000/",
		       "issue_id": "0000000000"
		     },
		     "triggered_rule": "example-alert-dev"
		   },
		   "actor": {
		     "type": "application",
		     "id": "sentry",
		     "name": "Sentry"
		   }
		 }
		""";

	private FakeSentryPayload() {
	}
}
