package com.sharipov.brainexercise.interactor

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sharipov.brainexercise.model.firebase.Exercise
import com.sharipov.brainexercise.model.firebase.TestType

class CategoriesInteractor {
    companion object {
        const val CATEGORIES = "categories"
    }

    fun getCategories(
        onError: (DatabaseError) -> Unit,
        onSuccess: (List<Exercise>) -> Unit
    ) = FirebaseDatabase.getInstance()
        .getReference(CATEGORIES)
        .addValueEventListener(
            object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) = onError(p0)

                override fun onDataChange(p0: DataSnapshot) = p0.children
                    .mapNotNull { it.getValue(Exercise::class.java) }
                    .filter { it.type != TestType.COLORS }
                    .run { onSuccess(this) }
            }
        )
}