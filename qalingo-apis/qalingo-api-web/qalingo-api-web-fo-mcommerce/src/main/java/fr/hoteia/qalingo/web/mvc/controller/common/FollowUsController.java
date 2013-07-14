/**
 * Most of the code in the Qalingo project is copyrighted Hoteia and licensed
 * under the Apache License Version 2.0 (release version 0.7.0)
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *                   Copyright (c) Hoteia, 2012-2013
 * http://www.hoteia.com - http://twitter.com/hoteia - contact@hoteia.com
 *
 */
package fr.hoteia.qalingo.web.mvc.controller.common;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import fr.hoteia.qalingo.core.domain.Localization;
import fr.hoteia.qalingo.core.domain.Market;
import fr.hoteia.qalingo.core.domain.MarketArea;
import fr.hoteia.qalingo.core.domain.MarketPlace;
import fr.hoteia.qalingo.core.domain.Retailer;
import fr.hoteia.qalingo.core.web.servlet.ModelAndViewThemeDevice;
import fr.hoteia.qalingo.web.mvc.controller.AbstractMCommerceController;
import fr.hoteia.qalingo.web.mvc.form.FollowUsForm;
import fr.hoteia.qalingo.web.mvc.viewbean.FollowUsViewBean;
import fr.hoteia.qalingo.web.service.WebCommerceService;

/**
 * 
 */
@Controller
public class FollowUsController extends AbstractMCommerceController {

	@Autowired
    protected WebCommerceService webCommerceService;
	
	@RequestMapping(value = "/follow-us.html*", method = RequestMethod.GET)
	public ModelAndView displayFollowUs(final HttpServletRequest request, final HttpServletResponse response, ModelMap modelMap) throws Exception{
		ModelAndViewThemeDevice modelAndView = new ModelAndViewThemeDevice(getCurrentVelocityPath(request), "follow-us/follow-us-form");
		
		// "followus";
		formFactory.buildFollowUsForm(request, modelAndView);
		
        return modelAndView;
	}

	@RequestMapping(value = "/follow-us.html*", method = RequestMethod.POST)
	public ModelAndView followUs(final HttpServletRequest request, final HttpServletResponse response, @Valid FollowUsForm followUsForm,
								BindingResult result, ModelMap modelMap) throws Exception {
		ModelAndViewThemeDevice modelAndView = new ModelAndViewThemeDevice(getCurrentVelocityPath(request), "follow-us/follow-us-success");

		// "followus";

		if (result.hasErrors()) {
			return displayFollowUs(request, response, modelMap);
		}

		webCommerceService.saveAndBuildNewsletterRegistrationConfirmationMail(request, followUsForm);
		
		final MarketPlace currentMarketPlace = requestUtil.getCurrentMarketPlace(request);
		final Market currentMarket = requestUtil.getCurrentMarket(request);
		final MarketArea currentMarketArea = requestUtil.getCurrentMarketArea(request);
		final Localization currentLocalization = requestUtil.getCurrentLocalization(request);
		final Retailer currentRetailer = requestUtil.getCurrentRetailer(request);
		final FollowUsViewBean followUs = viewBeanFactory.buildFollowUsViewBean(request, currentMarketPlace, currentMarket, currentMarketArea, currentLocalization, currentRetailer);
		modelAndView.addObject("followUs", followUs);

        return modelAndView;
	}
	
}