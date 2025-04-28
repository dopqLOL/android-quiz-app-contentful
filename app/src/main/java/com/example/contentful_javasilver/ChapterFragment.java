package com.example.contentful_javasilver;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.contentful_javasilver.adapter.ChapterAdapter;
import com.example.contentful_javasilver.databinding.FragmentChapterBinding;
import com.example.contentful_javasilver.model.ChapterItem;
import com.example.contentful_javasilver.viewmodels.QuizViewModel;

import java.util.ArrayList;
import java.util.List;

public class ChapterFragment extends Fragment implements ChapterAdapter.OnChapterClickListener {
    private FragmentChapterBinding binding;
    private ChapterAdapter chapterAdapter;
    private QuizViewModel quizViewModel;

    // 章のタイトルと説明
    private final String[] chapterTitles = {
            "Java の概要と簡単なJavaプログラムの作成",
            "Javaの基本データ型と文字列の操作",
            "演算子と制御構造",
            "クラスの定義とインスタンスの使用",
            "継承とインタフェースの使用",
            "例外処理"
    };

    private final String[] chapterDescriptions = {
            "Javaプログラムの基本構造とコンパイル・実行方法を学びます",
            "変数、データ型、配列、文字列操作の基本を習得します",
            "条件分岐や繰り返し処理など、プログラムの流れを制御する方法を学びます",
            "オブジェクト指向プログラミングの基礎となるクラスの定義と使用方法を学びます",
            "継承と多態性の概念を理解し、インタフェースを活用する方法を学びます",
            "例外処理の仕組みと実装方法についてマスターします"
    };

    // 各章のアイコンリソースID
    private final int[] chapterIconResIds = {
            R.drawable.ic_lesson_package, // 仮のアイコン
            R.drawable.ic_lesson_package,
            R.drawable.ic_lesson_package,
            R.drawable.ic_lesson_package,
            R.drawable.ic_lesson_package,
            R.drawable.ic_lesson_package
    };

    // 各章の色リソースID
    private final int[] chapterColorResIds = {
            R.color.unit_color_1,
            R.color.unit_color_2,
            R.color.unit_color_3,
            R.color.unit_color_4,
            R.color.unit_color_5,
            R.color.unit_color_6
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        quizViewModel = new ViewModelProvider(requireActivity()).get(QuizViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentChapterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupUI();
        setupRecyclerView();
        loadChapters();
        observeViewModel();
        quizViewModel.loadStatisticsData();
    }

    private void setupUI() {
        // プレミアムボタンの参照を削除
        // binding.includedAchievementsBanner.premiumButton.setOnClickListener(v -> {
        //     Toast.makeText(requireContext(), "プレミアム機能は現在開発中です", Toast.LENGTH_SHORT).show();
        // });
        // プロフィールボタンの参照を削除
        // binding.includedAchievementsBanner.profileButton.setOnClickListener(v -> {
        //     Toast.makeText(requireContext(), "プロフィール機能は現在開発中です", Toast.LENGTH_SHORT).show();
        // });
    }

    private void observeViewModel() {
        quizViewModel.getStreakInfo().observe(getViewLifecycleOwner(), streakPair -> {
            if (binding != null) { // bindingがnullでないことを確認
                int currentStreak = streakPair.first;
                 // fireAchievement の表示/非表示ロジックを修正 (XMLで制御するためJavaからは削除)
                if (currentStreak > 0) {
                    // binding.includedAchievementsBanner.fireAchievement.setVisibility(View.VISIBLE);
                    binding.includedAchievementsBanner.fireCount.setText(String.valueOf(currentStreak));
                } else {
                    // ストリーク0の場合、カウントを0に設定
                    binding.includedAchievementsBanner.fireCount.setText("0");
                    // binding.includedAchievementsBanner.fireAchievement.setVisibility(View.GONE);
                }
            }
        });

        // --- 追加: 今日の学習時間を監視 ---
        quizViewModel.getTodayStudyTimeLiveData().observe(getViewLifecycleOwner(), timeString -> {
            if (binding != null && binding.includedAchievementsBanner != null) {
                binding.includedAchievementsBanner.todayLearningTimeText.setText(timeString);
            }
        });
        // --- 追加ここまで ---
    }

    private void setupRecyclerView() {
        binding.chaptersRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        chapterAdapter = new ChapterAdapter(new ArrayList<>(), this);
        binding.chaptersRecyclerView.setAdapter(chapterAdapter);
    }

    private void loadChapters() {
        // 通常はデータベースから読み込むなどの処理が入る
        // ここではサンプルデータを作成
        List<ChapterItem> chapters = new ArrayList<>();
        
        for (int i = 0; i < chapterTitles.length; i++) {
            // 章番号は1から始まる
            int chapterNumber = i + 1;
            // 各章のカテゴリ数は固定（仮の値）
            int totalCategories = 5;
            // 完了したカテゴリ数（仮の値）
            int completedCategories = (int) (Math.random() * (totalCategories + 1));
            
            ChapterItem chapter = new ChapterItem(
                    chapterNumber,
                    chapterTitles[i],
                    chapterDescriptions[i],
                    totalCategories,
                    completedCategories,
                    chapterColorResIds[i],
                    chapterIconResIds[i]
            );
            
            chapters.add(chapter);
        }
        
        chapterAdapter.updateItems(chapters);
    }

    @Override
    public void onChapterClick(ChapterItem chapter) {
        navigateToCategoryFragment(binding.getRoot(), chapter.getChapterNumber(), chapter.getTitle());
    }

    private void navigateToCategoryFragment(View view, int chapterNumber, String chapterTitle) {
        // Bundleでデータを渡す
        Bundle args = new Bundle();
        args.putInt("chapterNumber", chapterNumber);
        args.putString("chapterTitle", chapterTitle);
        
        // CategoryFragmentに遷移
        Navigation.findNavController(view).navigate(R.id.action_chapterFragment_to_categoryFragment, args);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
