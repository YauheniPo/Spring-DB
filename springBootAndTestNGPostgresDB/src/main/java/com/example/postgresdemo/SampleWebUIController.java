package com.example.postgresdemo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/")
public class SampleWebUIController {

    @Autowired
    ValueRepository repository;

    @GetMapping
    public ModelAndView showValue() {
        List<Value> values = (List<Value>) repository.findAll();
        return new ModelAndView("index", "value",
                                Objects.requireNonNull(values.isEmpty() ? "" : values.stream().map(Object::toString)
                                    .collect(Collectors.joining(", "))));
    }

    @PostMapping("save")
    public ModelAndView save(@RequestParam("value") String value, RedirectAttributes redirect) {
        repository.save(new Value(value));
        return new ModelAndView("redirect:/");
    }

    @PostMapping("delete")
    public ModelAndView delete(@RequestParam("value") String value, RedirectAttributes redirect) {
        try {
            Long id = (long) Integer.parseInt(value);
            repository.findById(id).ifPresent(value1 -> repository.delete(value1));
        } catch (NumberFormatException e) {
            repository.findByValue(value).forEach(v -> repository.delete(v));
        }
        return new ModelAndView("redirect:/");
    }
}