package com.vertice.teepop.mangaapp.fragment

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.mlsdev.rximagepicker.RxImagePicker
import com.mlsdev.rximagepicker.Sources
import com.vertice.teepop.mangaapp.R
import com.vertice.teepop.mangaapp.util.RecyclerItemClickListener
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_add_new.*

/**
 * Created by nuuneoi on 11/16/2014.
 */
class AddMangaFragment : Fragment() {

    val TAG: String = this::class.java.simpleName

    val imageAdapter = ImageAdapter()

    lateinit var storageRef: StorageReference
    lateinit var imageRef: StorageReference

    companion object {
        fun newInstance(): AddMangaFragment {
            val fragment = AddMangaFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init(savedInstanceState)

        storageRef = FirebaseStorage.getInstance().reference
        imageRef = storageRef.child("image")

        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_new, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initInstances(savedInstanceState)
    }

    private fun init(savedInstanceState: Bundle?) {
        // Init Fragment level's variable(s) here
    }

    private fun initInstances(savedInstanceState: Bundle?) {
        // Init 'View' instance(s) with rootView.findViewById here

        imageCover.setOnClickListener {
            chooseImageCover()
        }

        initRecyclerView()

        btnSend.setOnClickListener {
            sendImageToFireBase()
        }
    }

    private fun sendImageToFireBase() {
        val dialog = ProgressDialog(context).apply {
            isIndeterminate = false
            progress = 0
            max = imageAdapter.uriList.size

            setCancelable(false)
            setMessage("Please Wait")
        }
        dialog.show()


        val observable = Observable.create<String> { emitter: ObservableEmitter<String> ->
            imageAdapter.uriList.forEach {
                imageRef.putFile(it)
                        .addOnFailureListener({ e ->
                            emitter.onError(e)
                        })
                        .addOnSuccessListener({ taskSnapshot ->
                            emitter.onNext(taskSnapshot.downloadUrl.toString())

                            if (it == imageAdapter.uriList.last())
                                emitter.onComplete()
                        })
            }
        }

        observable.subscribe(object : Observer<String?> {
            override fun onComplete() {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onSubscribe(d: Disposable?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onNext(t: String?) {
                Log.i(TAG, t)
                dialog.progress++

                if (dialog.progress == dialog.max)
                    dialog.dismiss()
            }

            override fun onError(e: Throwable?) {
                Toast.makeText(context, e?.message, Toast.LENGTH_SHORT).show()
                e?.printStackTrace()
                dialog.dismiss()
            }
        })

    }

    private fun chooseImageCover() {
        RxImagePicker.with(context)
                .requestImage(Sources.GALLERY)
                .subscribe { uri ->
                    imageCover.setImageURI(uri)
                }
    }

    private fun initRecyclerView() {
        val gridLayoutManager = GridLayoutManager(context, 3).apply {
            isAutoMeasureEnabled = true
        }

        recyclerView.apply {
            layoutManager = gridLayoutManager
            isNestedScrollingEnabled = false
            adapter = imageAdapter

            addOnItemTouchListener(RecyclerItemClickListener(context, onItemClickListener))
//            setHasFixedSize(true)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }

    /*
     * Save Instance State Here
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save Instance State here
    }

    /*
     * Restore Instance State Here
     */
    private fun onRestoreInstanceState(savedInstanceState: Bundle) {
        // Restore Instance State here
    }

    val onItemClickListener: (View, Int) -> Unit = { view, position ->
        if (position == imageAdapter.itemCount - 1) {
            RxImagePicker.with(context)
                    .requestMultipleImages()
                    .subscribe { uris ->
                        if (uris.isNotEmpty()) addImageUri(uris)
                    }
        }
    }

    private fun addImageUri(uri: List<Uri>) {
        imageAdapter.uriList.addAll(uri)
        imageAdapter.notifyDataSetChanged()
    }

    class ImageAdapter : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

        val uriList: MutableList<Uri> = ArrayList()

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ImageViewHolder {
            val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_add_image, parent, false)
            return ImageViewHolder(view)
        }

        override fun getItemCount(): Int {
            return uriList.size + 1
        }

        override fun onBindViewHolder(holder: ImageViewHolder?, position: Int) {
            if (position < uriList.size)
                holder?.setImage(uriList[position])
        }

        class ImageViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

            fun setImage(uri: Uri) {
                val imageView = view.findViewById<ImageView>(R.id.imageView)
                Glide.with(view.context)
                        .load(uri)
                        .into(imageView)
            }
        }
    }

}
