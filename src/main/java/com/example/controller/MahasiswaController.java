package com.example.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import com.example.model.Mahasiswa;

@Controller
public class MahasiswaController {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/")
    public String index(Model model) {
        String sql = "SELECT * FROM mahasiswa";
        List<Mahasiswa> mahasiswa = jdbcTemplate.query(sql,
                BeanPropertyRowMapper.newInstance(Mahasiswa.class));
        model.addAttribute("mahasiswa", mahasiswa);
        return "index";
    }

    @GetMapping("/add")
    public String add(Model model) {
        return "add";
    }

    @PostMapping("/add")
    public String add(Mahasiswa mahasiswa) {
        String sql = "INSERT INTO mahasiswa VALUES(?,?,?,?)";
        jdbcTemplate.update(sql, mahasiswa.getNim(),
                mahasiswa.getNama(),
                mahasiswa.getAngkatan(), mahasiswa.getGender());
        return "redirect:/";
    }

    @GetMapping("/edit/{nim}")
    public String edit(@PathVariable("nim") String nim, Model model) {
        String sql = "SELECT * FROM mahasiswa WHERE nim = ?";
        Mahasiswa mahasiswa = jdbcTemplate.queryForObject(sql, BeanPropertyRowMapper.newInstance(Mahasiswa.class), nim);
        model.addAttribute("mahasiswa", mahasiswa);
        return "edit";
    }

    @PostMapping("/edit")
    public String edit(Mahasiswa mahasiswa) {
        String sql = "UPDATE mahasiswa SET nama = ?, angkatan = ?, gender = ? WHERE nim = ?";
        jdbcTemplate.update(sql, mahasiswa.getNama(), mahasiswa.getAngkatan(), mahasiswa.getGender(),
                mahasiswa.getNim());
        return "redirect:/";
    }

    @GetMapping("/delete/{nim}")
    public String delete(@PathVariable("nim") String nim) {
        String sql = "DELETE FROM mahasiswa WHERE nim = ?";
        jdbcTemplate.update(sql, nim);
        return "redirect:/";
    }

    @GetMapping("/detail/{nim}")
    public String detail(@PathVariable("nim") String nim, Model model) {
        try {
            // Ambil data mahasiswa
            String sqlMahasiswa = "SELECT * FROM mahasiswa WHERE nim = ?";
            Mahasiswa mahasiswa = jdbcTemplate.queryForObject(
                sqlMahasiswa, 
                BeanPropertyRowMapper.newInstance(Mahasiswa.class), 
                nim
            );
            model.addAttribute("mahasiswa", mahasiswa);
    
            // Ambil data IRS beserta status kelulusan
            String sqlIRS = "SELECT i.ID_IRS, i.SEMESTER, i.MATKUL_ID, i.STATUS, " +
                            "mk.MATKUL_NAMA, mk.SKS, mk.HARI " +
                            "FROM IRS i " +
                            "JOIN MATA_KULIAH mk ON i.MATKUL_ID = mk.MATKUL_ID " +
                            "WHERE i.NIM = ?";
            
            List<Map<String, Object>> irsList = jdbcTemplate.queryForList(sqlIRS, nim);
            model.addAttribute("irsList", irsList);
    
        } catch (Exception e) {
            model.addAttribute("error", "Data mahasiswa tidak ditemukan atau belum memiliki IRS.");
            return "error";
        }
        return "detail";
    }    
}