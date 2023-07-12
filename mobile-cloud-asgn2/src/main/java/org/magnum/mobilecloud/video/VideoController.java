/*
 * 
 * Copyright 2014 Jules White
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package org.magnum.mobilecloud.video;

import java.security.Principal;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.magnum.mobilecloud.video.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

import com.google.common.collect.Lists;

import org.magnum.mobilecloud.video.repository.Video;

@Controller
public class VideoController {
	
	/**
	 * You will need to create one or more Spring controllers to fulfill the
	 * requirements of the assignment. If you use this file, please rename it
	 * to something other than "AnEmptyController"
	 * 
	 * 
		 ________  ________  ________  ________          ___       ___  ___  ________  ___  __       
		|\   ____\|\   __  \|\   __  \|\   ___ \        |\  \     |\  \|\  \|\   ____\|\  \|\  \     
		\ \  \___|\ \  \|\  \ \  \|\  \ \  \_|\ \       \ \  \    \ \  \\\  \ \  \___|\ \  \/  /|_   
		 \ \  \  __\ \  \\\  \ \  \\\  \ \  \ \\ \       \ \  \    \ \  \\\  \ \  \    \ \   ___  \  
		  \ \  \|\  \ \  \\\  \ \  \\\  \ \  \_\\ \       \ \  \____\ \  \\\  \ \  \____\ \  \\ \  \ 
		   \ \_______\ \_______\ \_______\ \_______\       \ \_______\ \_______\ \_______\ \__\\ \__\
		    \|_______|\|_______|\|_______|\|_______|        \|_______|\|_______|\|_______|\|__| \|__|
                                                                                                                                                                                                                                                                        
	 * 
	 */
	@Autowired
	private VideoRepository videoRepo;
	@RequestMapping(value="/go",method=RequestMethod.GET)
	public @ResponseBody String goodLuck(){
		return "Good Luck!";
	}
	@RequestMapping(value="/video",method=RequestMethod.GET)
	public @ResponseBody Iterable<Video> getAllVideo(){
		return Lists.newArrayList(videoRepo.findAll());
	}
	@RequestMapping(value="/video",method=RequestMethod.POST)
	public @ResponseBody Video postVideo(@RequestBody Video v){
//		v.setUrl(getDataUrl(v.getId()));
//		v.setLikes(0);
//		v.setLikedBy(new HashSet<String>());
		return videoRepo.save(v);
	}
	@RequestMapping(value="/video/{id}",method=RequestMethod.GET)
	public @ResponseBody Optional<Video> getVideoById(@PathVariable("id") Long id){

		return videoRepo.findById(id);
	}
	@RequestMapping(value="/video/{id}/like",method=RequestMethod.POST)
	public @ResponseBody void voteLikeById(
			@PathVariable("id") Long id,
			Principal p){
		
		Optional<Video>videoOpt=videoRepo.findById(id);
		if(!videoOpt.isPresent()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		Video video=videoOpt.get();
		Set<String>LikedBy=video.getLikedBy();
		if(LikedBy.contains(p.getName())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}
		LikedBy.add(p.getName());
		video.setLikedBy(LikedBy);
		video.setLikes(video.getLikes()+1);
		videoRepo.save(video);
//		return true;
	}
	@RequestMapping(value="/video/{id}/unlike",method=RequestMethod.POST)
	public @ResponseBody void voteUnlikeById(
			@PathVariable("id") Long id,
			Principal p){
		
		Optional<Video>videoOpt=videoRepo.findById(id);
		if(!videoOpt.isPresent()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
//			return false;
		}
		Video video=videoOpt.get();
		Set<String>LikedBy=video.getLikedBy();
		if(LikedBy.contains(p.getName())==false) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
//			return false;
		}
		LikedBy.remove(p.getName());
		video.setLikedBy(LikedBy);
		video.setLikes(video.getLikes()-1);
		videoRepo.save(video);
//		return true;
	}
	@RequestMapping(value="/video/search/findByName",method=RequestMethod.GET)
	public @ResponseBody Collection<Video> getFindByName(@RequestParam("title")String title){
		Collection<Video> videos=null;
		try {
			videos=videoRepo.findByName(title);
		}catch(Exception e) {
			System.out.println("*".repeat(40));
			System.out.println(e.getMessage());
		}
		return videos;
	}
	@RequestMapping(value="/video/search/findByDurationLessThan",method=RequestMethod.GET)
	public @ResponseBody Collection<Video> getFindByDurationLessThan(@RequestParam("duration") Long maxduration){
		return videoRepo.findByDurationLessThan(maxduration);
	}
}
