$(document).ready(
		function() {
			var scripts = document.scripts;
			var url = scripts[scripts.length - 1].src;
			var ctx = new URL(url).param('ctx') || '';

			// themes, change CSS with JS
			// default theme(CSS) is cerulean, change it if needed
			var defaultTheme = 'cerulean';
			var currentTheme = $.cookie('currentTheme') == null ? defaultTheme : $.cookie('currentTheme');
			var msie = navigator.userAgent.match(/msie/i);
			$.browser = {};
			$.browser.msie = {};
			switchTheme(currentTheme);

			$('.navbar-toggle').click(function(e) {
				e.preventDefault();
				$('.nav-sm').html($('.navbar-collapse').html());
				$('.sidebar-nav').toggleClass('active');
				$(this).toggleClass('active');
			});

			var $sidebarNav = $('.sidebar-nav');

			// Hide responsive navbar on clicking outside
			$(document).mouseup(
					function(e) {
						// if the target of the click isn't the container...
						if (!$sidebarNav.is(e.target) && $sidebarNav.has(e.target).length === 0 && !$('.navbar-toggle').is(e.target) && $('.navbar-toggle').has(e.target).length === 0
								&& $sidebarNav.hasClass('active')) {
							e.stopPropagation();
							$('.navbar-toggle').click();
						}
					});

			$('#themes a').click(function(e) {
				e.preventDefault();
				currentTheme = $(this).attr('data-value');
				$.cookie('currentTheme', currentTheme, {
					expires : 365
				});
				switchTheme(currentTheme);
			});

			function switchTheme(themeName) {
				if (themeName == 'classic') {
					$('#bs-css').attr('href', ctx + '/bcs/bootstrap/dist/css/bootstrap.min.css');
				} else {
					$('#bs-css').attr('href', ctx + '/css/bootstrap-' + themeName + '.min.css');
				}

				$('#themes i').removeClass('glyphicon glyphicon-ok whitespace').addClass('whitespace');
				$('#themes a[data-value=' + themeName + ']').find('i').removeClass('whitespace').addClass('glyphicon glyphicon-ok');
			}

			// highlight current / active link
			$('ul.main-menu li a').each(function() {
				if ($($(this))[0].href == String(window.location))
					$(this).parent().addClass('active');
			});

			// establish history variables
			var History = window.History, // Note: We are using a
			// capital H instead of a lower h
			State = History.getState(), $log = $('#log');

			// bind to State Change
			History.Adapter.bind(window, 'statechange', function() {
				// Note: We are using statechange instead of popstate
				var State = History.getState();
				// Note: We are using History.getState() instead of event.state
				$.ajax({
					url : State.url,
					success : function(msg) {
						$('#content').html($(msg).find('#content').html());
						$('#loading').remove();
						$('#content').fadeIn();
						var newTitle = $(msg).filter('title').text();
						$('title').text(newTitle);
						docReady();
					}
				});
			});

			$.ajaxSetup({
				error : function(jqXHR, textStatus, errorThrown) {
					var msg = "未知错误,你确定不是黑客或火星人？";
					switch (jqXHR.status) {
					case (500):
						msg = "服务器系统内部错误,请稍后再试.";
						break;
					case (401):
						msg = "登录会话失效，请重新登录系统.";
						$("#confirm").click(function() {
							window.location = "index.jsp"
						});
						break;
					case (403):
						msg = "无权限执行此操作,请联系管理员.";
						break;
					case (408):
						msg = "请求超时,请稍后再试.";
						break;
					}
					$("#errorDialog .modal-body p").html(msg);
					$("#errorDialog").modal('show');
				}
			});

			// other things to do on document ready, separated for ajax calls
			docReady();
		});

function docReady() {
	// prevent # links from moving to top
	$('a[href="#"][data-top!=true]').click(function(e) {
		e.preventDefault();
	});

	$('.btn-close').click(function(e) {
		e.preventDefault();
		$(this).parent().parent().parent().fadeOut();
	});

	$('.btn-minimize').click(function(e) {
		e.preventDefault();
		var $target = $(this).parent().parent().next('.box-content');
		if ($target.is(':visible'))
			$('i', $(this)).removeClass('glyphicon-chevron-up').addClass('glyphicon-chevron-down');
		else
			$('i', $(this)).removeClass('glyphicon-chevron-down').addClass('glyphicon-chevron-up');
		$target.slideToggle();
	});

	$('.btn-setting').click(function(e) {
		e.preventDefault();
		$('#myModal').modal('show');
	});

	autosize(document.querySelectorAll('textarea'));
}

function URL(url) {
	this.url = url;
	this.param = function(key) {
		var reg = new RegExp("(\\?|&)" + key + "=([^&]*)(&|$)");
		var r = url.substr(1).match(reg);
		if (r != null)
			return unescape(r[2]);
		return null;
	};

	return this;
}