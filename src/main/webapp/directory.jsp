<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>爱读--${article.name}目录</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="shortcut icon" href="${contextPath}/img/favicon.ico">
<link id="bs-css" href="${contextPath}/css/bootstrap-cerulean.min.css" rel="stylesheet">
<link href="${contextPath}/css/charisma-app.css" rel="stylesheet">
<script src="${contextPath}/bcs/jquery/jquery.min.js"></script>
<script src="${contextPath}/bcs/bootstrap/dist/js/bootstrap.min.js"></script>
<script src="${contextPath}/js/jquery.cookie.js"></script>
<script src="${contextPath}/js/jquery.history.js"></script>
<script src="${contextPath}/js/charisma.js"></script>
</head>
<body>
	<jsp:include page="topbar.jsp"></jsp:include>
	<div class="ch-container">
		<div class="row">
			<div id="menu" class="col-sm-2 col-lg-2">
				<jsp:include page="menu.jsp"></jsp:include>
			</div>
			<div id="content" class="col-lg-10 col-sm-10">
				<div class="row">
					<div class="box col-md-12">
						<div class="box-inner">
							<div class="box-header well" data-original-title="">
								<h2>
									<i class="glyphicon glyphicon-th"></i> ${article.name}
								</h2>
								<div class="box-icon">
									<a href="#" class="btn btn-minimize btn-round btn-default"><i class="glyphicon glyphicon-chevron-up"></i></a>
								</div>
							</div>
							<div class="box-content">
								<table class="table table-condensed">
									<thead>
										<tr>
											<th>作者:${article.author}</th>
											<th>分类:${article.category.name}</th>
											<th>状态:${article.status}</th>
											<th><a href="${contextPath}/view/article/download/${article.id}">下载</a></th>
										</tr>
									</thead>
									<tbody>
										<tr class="line">
											<td colspan="4"><p class="btn-group">
													<c:forEach items="${article.parts}" var="part">
														<a class="btn btn-default" href="${contextPath}/view/article/read/${article.id}-${part.index}">第${part.index}节</a>
													</c:forEach>
												</p></td>
										</tr>
									</tbody>
								</table>

							</div>
						</div>
					</div>
				</div>
			</div>
			<!-- content ends -->
			<!--/#content.col-md-0-->
		</div>
		<!--/fluid-row-->
		<hr>
		<jsp:include page="footer.jsp"></jsp:include>
	</div>
	<!--/.fluid-container-->
</body>
</html>