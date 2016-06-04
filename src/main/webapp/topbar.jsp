<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<div class="navbar navbar-default" role="navigation">
	<div class="navbar-inner">
		<button type="button" class="navbar-toggle pull-left animated flip">
			<span class="sr-only">Toggle navigation</span> <span class="icon-bar"></span> <span class="icon-bar"></span> <span class="icon-bar"></span>
		</button>
		<a class="navbar-brand" href="${contextPath}/index.jsp"><img alt="Charisma Logo" src="${contextPath}/img/logo20.png" class="hidden-xs" /><span>ireader</span></a>
		<!-- theme selector starts -->
		<div class="btn-group pull-right theme-container animated tada">
			<button class="btn btn-default dropdown-toggle" data-toggle="dropdown">
				<i class="glyphicon glyphicon-tint"></i><span class="hidden-sm hidden-xs"> Change Theme / Skin</span> <span class="caret"></span>
			</button>
			<ul class="dropdown-menu" id="themes">
				<li><a data-value="cerulean" href="#"><i class="whitespace"></i> Cerulean</a></li>
				<li><a data-value="classic" href="#"><i class="whitespace"></i> Classic</a></li>
				<li><a data-value="simplex" href="#"><i class="whitespace"></i> Simplex</a></li>
				<li><a data-value="slate" href="#"><i class="whitespace"></i> Slate</a></li>
			</ul>
		</div>
		<!-- theme selector ends -->
		<ul class="collapse navbar-collapse nav navbar-nav top-menu">
			<li>
				<form class="navbar-search pull-left" action="${contextPath}/view/article/query">
					<input placeholder="搜索" class="search-query form-control col-md-12" name="word" type="text">
				</form>
			</li>
		</ul>
	</div>
</div>